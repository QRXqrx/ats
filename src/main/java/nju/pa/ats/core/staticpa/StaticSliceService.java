package nju.pa.ats.core.staticpa;

import nju.pa.ats.core.result.AtomTestCase;

import java.util.List;

/**
 * Methods that a staticpa should contain.
 *
 * @author QRX
 * @email QRXwzx@outlook.com
 * @date 2020-02-13
 */
public interface StaticSliceService {

    /**
     * Utilize a staticpa's own configuration, do backward-style slice.
     *
     * @return A list of atom test cases.
     */
    List<AtomTestCase> slice();


}
