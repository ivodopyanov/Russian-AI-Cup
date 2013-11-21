public interface Constants
{

    // Кол-во ХП, при котором боец считается раненым (и нуждается в лечении)
    int WOUNDED_HP = 70;
    //Кол-во ХП, при котором боец считается смертельно раненым (и должен избегать перестрелки в любом случае)
    int DEADLY_WOUNDED_HP = 30;

    //Максимальное расстояние лидера от взвода
    int MAX_TEAM_DISTANCE_FROM_LEADER = 8;

    //Оценка необходимости подобрать бонус
    double BONUS_MOVE_EVALUATION = 5.0;
    //Оценка необходимости держать строй
    double KEEP_FORMATION_MOVE_EVALUATION = 2.0;
    //Оценка глобального перемещения взвода
    double LONG_TERM_MOVE_EVALUATION = 1.0;
    //Оценка стрельбы по противнику
    double SHOOT = 20.0;
    //Оценка необходимости передвижения к укрытию
    double ESCAPE = 40.0;
    //Оценка необходимости перемещения к раненому бойцу
    double GO_TO_WOUNDED_TEAMMATE = 25.0;
    //Оценка необходимости лечения медиком или аптечкой
    double HEAL = 30;
    //Оценка броска гранаты
    double THROW_GRENADE = 25.0;
    //Оценка движения к месту, откуда можно эффективно бросить гранату
    double GO_TO_THROW_GRENADE_POSITION = 22.0;

}
