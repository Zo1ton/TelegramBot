public enum Tunes {
    startMsg("Оле-Оле! Физкульт-привет!\nВас приветствует телеграм бот ростовского сообщества СБТ-футболистов."),
    dbFile("temp.out");

    private final String tune;

    Tunes(String tune) {
        this.tune = tune;
    }

    public String getTune() {
        return tune;
    }
}