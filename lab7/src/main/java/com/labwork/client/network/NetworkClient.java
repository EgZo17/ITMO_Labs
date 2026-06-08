package com.labwork.client.network;

import com.labwork.common.protocol.Request;
import com.labwork.common.protocol.Response;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * Клиентский модуль сетевого взаимодействия.
 * Использует SocketChannel в НЕБЛОКИРУЮЩЕМ режиме (NIO).
 * Протокол: [4 байта длина][сериализованные данные]
 */

public class NetworkClient {
    private final SocketChannel socketChannel;
    private final Selector selector;

    public NetworkClient(String host, int port) throws IOException {
        this.socketChannel = SocketChannel.open();
        this.socketChannel.configureBlocking(false);
        this.selector = Selector.open();

        socketChannel.connect(new InetSocketAddress(host, port));
        socketChannel.register(selector, SelectionKey.OP_CONNECT);

        long startTime = System.currentTimeMillis();
        while (!socketChannel.finishConnect()) {
            if (selector.select(1000) == 0) {
                if (System.currentTimeMillis() - startTime > 10000) {
                    throw new IOException("Connection timeout");
                }
            }
        }
        
        socketChannel.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        System.out.println("Connected to server at " + host + ":" + port);
    }

    public void send(Request request) throws IOException {
        byte[] data = serialize(request);
        ByteBuffer buffer = ByteBuffer.allocate(4 + data.length);
        buffer.putInt(data.length);
        buffer.put(data);
        buffer.flip();
        
        while (buffer.hasRemaining()) {
            int written = socketChannel.write(buffer);
            if (written == 0) {
                selector.select(100);
            }
        }
    }

    public Response receive() throws IOException, ClassNotFoundException {
        // 1. Читаем заголовок (4 байта = длина)
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
        while (lengthBuffer.hasRemaining()) {
            if (selector.selectNow() == 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); //  Восстанавливаем статус прерывания
                    throw new IOException("Ожидание данных прервано", e);
                }
                continue;
            }
            
            for (SelectionKey key : selector.selectedKeys()) {
                selector.selectedKeys().remove(key);
                if (key.isReadable()) {
                    int bytesRead = socketChannel.read(lengthBuffer);
                    if (bytesRead == -1) throw new IOException("Сервер закрыл соединение");
                }
            }
        }
        
        lengthBuffer.flip();
        int dataLength = lengthBuffer.getInt();
        
        // 2. Читаем тело ответа
        ByteBuffer dataBuffer = ByteBuffer.allocate(dataLength);
        while (dataBuffer.hasRemaining()) {
            if (selector.selectNow() == 0) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Ожидание данных прервано", e);
                }
                continue;
            }
            
            for (SelectionKey key : selector.selectedKeys()) {
                selector.selectedKeys().remove(key);
                if (key.isReadable()) {
                    int bytesRead = socketChannel.read(dataBuffer);
                    if (bytesRead == -1) throw new IOException("Сервер закрыл соединение");
                }
            }
        }
        
        // 3. ⚠️ КРИТИЧЕСКИ ВАЖНО: правильно извлекаем байты для десериализации
        dataBuffer.flip();
        byte[] data = new byte[dataBuffer.remaining()];
        dataBuffer.get(data);
        
        return (Response) deserialize(data);
    }

    public void close() throws IOException {
        selector.close();
        socketChannel.close();
    }

    private byte[] serialize(Object obj) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
            return baos.toByteArray();
        }
    }

    private Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            return ois.readObject();
        }
    }
}
