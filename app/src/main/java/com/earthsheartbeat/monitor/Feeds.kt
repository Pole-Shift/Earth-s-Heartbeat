package com.earthsheartbeat.monitor

import java.net.URLEncoder

/** A single Schumann / ELF-VLF image feed. */
data class Feed(
    val id: String,
    val label: String,
    val source: String,
    val url: String
)

object Feeds {

    /** Base of the dashboard deployment. */
    const val SITE = "https://pole-shift.observer/app2/"

    /** The full page the app opens in its WebView. */
    const val PAGE = SITE + "index.html"

    /** Same proxy the web dashboard uses (fixes http + hotlink for widgets). */
    private const val PROXY = SITE + "proxy.php?url="

    /** Route an upstream image through the proxy with a cache-buster. */
    fun proxied(url: String): String {
        val enc = URLEncoder.encode(url, "UTF-8")
        return PROXY + enc + "&_cb=" + System.currentTimeMillis()
    }

    /** All feeds, Schumann-relevant ones first (offered in the widget picker). */
    val ALL: List<Feed> = listOf(
        // --- Schumann Resonance ---
        Feed("sr_dynspec", "SR Dynamic Spectrum", "Nagycenk (HU)",
            "https://nckobs.hu/data/sr/SR_dynspec_codemo_latest.png"),
        Feed("sr_shm", "Schumann Resonance", "sos70 (RU)",
            "https://sos70.ru/provider.php?file=shm.jpg"),
        // --- ELF / VLF coil receivers ---
        Feed("coil_lot", "Coil Receiver 8h", "Lot-et-Garonne (FR)",
            "http://www.vlf.it/cumiana/lotetgaronne_last-coil_8h.jpg"),
        Feed("coil_etna", "Coil Receiver 8h", "Etna (IT)",
            "http://www.etna-ero.it/live_etna/last-coil_8h.jpg"),
        Feed("e_vlf", "Electric VLF (E-field)", "Cumiana (IT)",
            "http://www.vlf.it/cumiana/last_E-VLF.jpg"),
        Feed("plotted", "Combined Plot", "Cumiana (IT)",
            "http://www.vlf.it/cumiana/last-plotted.jpg"),
        // --- VLF stations, Cumiana network ---
        Feed("sosenattos", "Sos Enattos Mine", "Sardinia (IT)",
            "http://www.vlf.it/cumiana/last-sosenattos-mine.jpg"),
        Feed("virgo_rdf", "Virgo RDF", "Cascina (IT)",
            "http://www.vlf.it/cumiana/last-virgo-rdf.jpg"),
        Feed("virgo_lr", "Virgo LR", "Cascina (IT)",
            "http://www.vlf.it/cumiana/last-virgo-LR.jpg"),
        Feed("geomar", "GEOMAR", "Cumiana (IT)",
            "http://www.vlf.it/cumiana/last-geomar.jpg"),
        // --- Solar / geomagnetic ---
        Feed("umf", "Magnetic Field", "sos70 (RU)",
            "https://sos70.ru/provider.php?file=umf.jpg"),
        Feed("srf", "Solar Flux", "sos70 (RU)",
            "https://sos70.ru/provider.php?file=srf.jpg"),
        Feed("xray", "GOES X-Ray (C/M/X)", "sos70 (RU)",
            "https://sos70.ru/provider.php?file=xraycmx.jpg")
    )

    fun byId(id: String?): Feed = ALL.firstOrNull { it.id == id } ?: ALL[0]
}
