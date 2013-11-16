/**
 * 
 */
package helpers;

import model.Trooper;

/**
 * @author ivodopyanov
 * @since 11 нояб. 2013 г.
 *
 */
public class WeightFunctions
{
    /**
     * Функция веса бонуса в зависимости от его очередности в массиве, отсортированном по важности бонуса
     * Для первого результат равен 5
     * Для второго 2.5
     */
    public static double bonusWeightFunction(int bonusRatingByImportance)
    {
        return Constants.BONUS_MOVE_EVALUATION / (bonusRatingByImportance + 1);
    }

    /**
     * Функция веса важности лечения солдата в зависимости от кол-ва его ХП и расстояния до него
     */
    public static double teammateHealFunction(Trooper medic, Trooper teammate)
    {
        int distance = DistanceCalculator.INSTANCE.getDistance(medic.getX(), medic.getY(), teammate.getX(),
                teammate.getY());
        return Constants.GO_TO_WOUNDED_TEAMMATE / (teammate.getHitpoints() / 20 + 1) * (distance / 5 + 1);
    }

    /**
     * Функция веса важности броска гранаты. Если гранату можно бросить только в одного противника, то значение будет 15 - меньше, чем вес выстрела
     * Но бросок гранаты заденет нескольких - то однозначно надо кидать
     */
    public static double throwGrenade(double damage)
    {
        return Constants.THROW_GRENADE + damage - 80;
    }
}
