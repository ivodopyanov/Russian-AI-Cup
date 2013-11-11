/**
 * 
 */
package helpers;

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
}
