package nju.pa.ats.core.text;

/**
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-03-05
 */
public interface SliceTextService {

    String generateNewTestClassContent();

    String outputNewTestClassTo(String outputDir) throws Exception;

}
