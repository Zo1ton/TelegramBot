public enum Tunes {
    startMsg("Вас приветствует телеграм бот ростовского сообщества СБТ-футболистов"),
    dbFile("temp.out");

    private final String tune;

    Tunes(String tune) {
        this.tune = tune;
    }

    public String getTune() {
        return tune;
    }
}