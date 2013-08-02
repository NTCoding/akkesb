import org.scalatest.FreeSpec
import org.scalatest.MustMatchers

class Date_range_extraction_specs extends FreeSpec with MustMatchers {

    "Parses a 2 digit 3 digit combo" in {
        Indexer.extractPay("$70k - $150k/year") must equal((70, 150))
    }

    "Parses a 2 digit 2 digit combo" in {
        Indexer.extractPay("$40k - $90k/year") must equal((40, 90))
    }

    "Parses a 3 digit 3 digit combo" in {
        Indexer.extractPay("$100k - $200k/year") must equal((100, 200))
    }

    "Returns 0,0 for everything else" in {
        Indexer.extractPay("N/A") must equal((0, 0))
        Indexer.extractPay("100") must equal((0, 0))
        Indexer.extractPay("200") must equal((0, 0))
    }
}
