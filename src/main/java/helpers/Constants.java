package helpers;

public interface Constants
{

    // Кол-во ХП, при котором боец считается раненым (и нуждается в лечении)
    int WOUNDED_HP = 70;

    int WORLD_WIDTH = 30;
    int WORLD_HEIGHT = 20;

    //Оценка необходимости подобрать бонус
    double BONUS_MOVE_EVALUATION = 5.0;
    //Оценка необходимости держать строй
    double KEEP_FORMATION_MOVE_EVALUATION = 2.0;
    //Оценка глобального перемещения взвода
    double LONG_TERM_MOVE_EVALUATION = 1.0;
}
