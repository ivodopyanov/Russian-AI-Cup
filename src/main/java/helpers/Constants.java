package helpers;

public interface Constants
{

    // Кол-во ХП, при котором боец считается раненым (и нуждается в лечении)
    int WOUNDED_HP = 70;
    //Кол-во ХП, при котором боец считается смертельно раненым (и должен избегать перестрелки в любом случае)
    int DEADLY_WOUNDED_HP = 30;

    int WORLD_WIDTH = 30;
    int WORLD_HEIGHT = 20;

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
    double GO_TO_WOUNDED_TEAMMATE = 25;
    //Оценка необходимости лечения медиком или аптечкой
    double HEAL = 30;
    //Оценка броска гранаты
    double THROW_GRENADE = 15.0;
}
