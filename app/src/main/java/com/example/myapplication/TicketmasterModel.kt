// Ticketmaster'dan gelen ana yanıtı temsil eder
data class TicketmasterResponse(
    val _embedded: Embedded
)

// "Embedded" alanındaki events listesine erişmek için
data class Embedded(
    val events: List<TicketmasterEvent>  // Burada Event yerine TicketmasterEvent kullanıyoruz
)

// Bir etkinliği temsil eder
data class TicketmasterEvent(
    val name: String,
    val dates: EventDates,
    val classifications: List<Classification>
)

// Etkinlik tarihlerini temsil eder
data class EventDates(
    val start: EventStart
)

// Etkinlik başlangıç tarihini ve saatini temsil eder
data class EventStart(
    val localDate: String,
    val localTime: String
)

// Etkinlik kategorisini (segmenti) temsil eder
data class Classification(
    val segment: Segment
)

data class Segment(
    val name: String
)
