package dk.itu.moapd.scootersharing.jonli

import dk.itu.moapd.scootersharing.jonli.models.Scooter
import dk.itu.moapd.scootersharing.jonli.utils.Utils.getCloseScooter
import org.junit.Test

class UtilsTest {

    @Test
    fun get_close_scooter_is_nearest() {
        val scooters = arrayListOf<Pair<String?, Scooter?>>()

        val s1 = Scooter(
            "1",
            1,
            false,
            "1",
            15.0,
            15.0,
            "1",
            false,
        )

        val s2 = Scooter(
            "2",
            1,
            true,
            "1",
            20.0,
            20.0,
            "1",
            false,
        )

        val s3 = Scooter(
            "3",
            1,
            true,
            "1",
            25.0,
            25.0,
            "1",
            false,
        )

        val s4 = Scooter(
            "4",
            1,
            true,
            "1",
            30.0,
            30.0,
            "1",
            false,
        )

        scooters.add(Pair("1", s1))
        scooters.add(Pair("2", s2))
        scooters.add(Pair("3", s3))
        scooters.add(Pair("4", s4))

        val res = getCloseScooter(scooters, 14.0, 14.0)

        assert(res == "2")
    }
}
