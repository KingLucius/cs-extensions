// use an integer for version numbers
version = 4

cloudstream {
    language = "en"
    // All of these properties are optional, you can safely remove them

    description = "Xtream IPTV Live categories, use \"Clone site\" feature and add settings as url,user,pass to pass server details."
    authors = listOf("KingLucius")

    /**
     * Status int as the following:
     * 0: Down
     * 1: Ok
     * 2: Slow
     * 3: Beta only
     * */
    status = 1 // will be 3 if unspecified
    tvTypes = listOf(
        "Live",
    )

    iconUrl = "https://raw.githubusercontent.com/KingLucius/cs-extensions/master/XtreamIPTV/icon.jpg"
}