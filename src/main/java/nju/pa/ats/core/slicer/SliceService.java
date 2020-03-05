package nju.pa.ats.core.slicer;

import nju.pa.ats.core.result.AtomTestCase;

import java.util.List;

/**
 * Methods that a slicer should contain.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-02-13
 */
public interface SliceService {

    /**
     * Utilize a slicer's own configuration, do backward-style slice.
     *
     * @return A list of atom test cases.
     */
    List<AtomTestCase> slice();


}
