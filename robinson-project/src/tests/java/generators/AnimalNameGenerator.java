package generators;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import enums.AnimalType;

public class AnimalNameGenerator {
    private static final Random random = new Random();
    
    private static final List<String> PARROT_NAMES = Arrays.asList(
        "Кеша", "Гоша", "Рио", "Ара", "Лори", "Жако", "Какаду", "Пират",
        "Капитан", "Флинт", "Джек", "Чико", "Попка", "Коко", "Лулу", "Пиксель",
        "Скарлет", "Эйс", "Зорро", "Бандит", "Шкипер", "Гром", "Эхо", "Квик",
        "Феникс", "Икар", "Яго", "Педро", "Феликс", "Арчи", "Пончо", "Рики",
        "Чарли", "Дино", "Элвис", "Фредди", "Гарри", "Инди", "Джонни", "Кевин"
    );
    
    private static final List<String> FOX_NAMES = Arrays.asList(
        "Рыжик", "Лисичка", "Огнехвост", "Патрикеевна", "Алиса", "Зверь",
        "Вук", "Тод", "Ренар", "Лис", "Фокси", "Ред", "Эмбер", "Скарлет",
        "Русти", "Коппер", "Гингер", "Фенек", "Вульпи", "Сильвер", "Рыжуля",
        "Плутовка", "Хитрюга", "Бусинка", "Искорка", "Малинка", "Ягодка",
        "Цыпа", "Шалун", "Проказник", "Следопыт", "Охотник", "Стриж", "Ветер"
    );
    
    private static final List<String> GOAT_NAMES = Arrays.asList(
        "Белянка", "Рогач", "Маня", "Пушистик", "Барашек", "Кудряшка",
        "Милка", "Зорька", "Ночка", "Снежка", "Буренка", "Цветик", "Люська",
        "Машка", "Глаша", "Дымка", "Ивушка", "Ласка", "Нежка", "Ряба",
        "Звездочка", "Искра", "Капелька", "Ласточка", "Метелица", "Огонек",
        "Роса", "Стрелка", "Тучка", "Умка", "Хлопушка", "Цаца", "Чара", "Юла"
    );
    
    private static final List<String> TURTLE_NAMES = Arrays.asList(
        "Тортила", "Сплинтер", "Доннелло", "Микеланджело", "Рафаэль", "Леонардо",
        "Шелдон", "Спот", "Панцирь", "Медли", "Старина", "Мудрец", "Атлант",
        "Омега", "Хронос", "Титан", "Башмак", "Броня", "Крепыш", "Ветеран",
        "Бублик", "Валун", "Громада", "Дозор", "Ерш", "Зодиак",
        "Кварц", "Лапоть", "Монолит", "Нептун", "Овал", "Плинтус", "Риф"
    );
    
    private static final List<String> PIGEON_NAMES = Arrays.asList(
        "Гоша", "Сильвер", "Грей", "Скай", "Винг", "Физзи", "Куак", "Персик",
        "Пухлик", "Крылышко", "Гомер", "Рокки", "Феникс", "Икар", "Зефир",
        "Облачко", "Снежок", "Пеппер", "Соль", "Перец", "Бублик", "Ворк",
        "Гуля", "Дутыш", "Жулик", "Курлык", "Летун", "Мотылек", "Пискун",
        "Сизый", "Тучка", "Хохолок", "Цыпа", "Чирик", "Шустрый"
    );
    
    public static String getRandomParrotName() {
        return getRandomName(PARROT_NAMES);
    }
    
    public static String getRandomFoxName() {
        return getRandomName(FOX_NAMES);
    }
    
    public static String getRandomGoatName() {
        return getRandomName(GOAT_NAMES);
    }
    
    public static String getRandomTurtleName() {
        return getRandomName(TURTLE_NAMES);
    }
    
    public static String getRandomPigeonName() {
        return getRandomName(PIGEON_NAMES);
    }
    
    public static String getRandomNameForType(AnimalType type) {
        switch (type) {
            case PARROT:
                return getRandomParrotName();
            case FOX:
                return getRandomFoxName();
            case GOAT:
                return getRandomGoatName();
            case TURTLE:
                return getRandomTurtleName();
            case PIGEON:
                return getRandomPigeonName();
            default:
                return "Безымянный";
        }
    }
    
    public static String getRandomName(List<String> nameList) {
        if (nameList == null || nameList.isEmpty()) {
            return "Безымянный";
        }
        return nameList.get(random.nextInt(nameList.size()));
    }
}
