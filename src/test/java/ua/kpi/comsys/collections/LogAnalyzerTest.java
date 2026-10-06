package ua.kpi.comsys.collections;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class LogAnalyzerTest {

    private final LogAnalyzer analyzer = new LogAnalyzer();

    // перевіряємо унікальні ip
    @Test
    void shouldReturnUniqueIps() {
        List<String> logs = List.of(
                log("127.0.0.1"),
                log("192.168.1.1"),
                log("127.0.0.1")
        );
        Set<String> result = analyzer.getUniqueIps(logs);

        assertEquals(
                Set.of("127.0.0.1", "192.168.1.1"),
                result
        );
    }

    @Test
    void shouldReturnEmptySetForEmptyList() {
        Set<String> result = analyzer.getUniqueIps(List.of());

        assertTrue(result.isEmpty());
    }

    // неправильні рядки мають ігноруватися
    @Test
    void shouldIgnoreInvalidLogLinesForUniqueIps() {
        List<String> logs = List.of(
                log("127.0.0.1"),
                "wrong log line",
                "hello world",
                log("192.168.1.1")
        );
        Set<String> result = analyzer.getUniqueIps(logs);

        assertEquals(
                Set.of("127.0.0.1", "192.168.1.1"),
                result
        );
    }
    @Test
    void shouldThrowExceptionWhenLogLinesAreNullForUniqueIps() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.getUniqueIps(null)
        );

        assertEquals(
                "logLines cannot be null",
                exception.getMessage()
        );
    }

    // перевіряємо найчастіші ip
    @Test
    void shouldReturnTopIpsByFrequency() {
        List<String> logs = List.of(
                log("10.0.0.1"),
                log("10.0.0.1"),
                log("10.0.0.1"),
                log("192.168.1.1"),
                log("192.168.1.1"),
                log("127.0.0.1")
        );

        List<String> result = analyzer.getTopIps(logs, 2);

        assertEquals(
                List.of("10.0.0.1", "192.168.1.1"),
                result
        );
    }

    // при однаковій частоті сортуємо за ip
    @Test
    void shouldSortAlphabeticallyWhenFrequencyIsEqual() {
        List<String> logs = List.of(
                log("192.168.1.2"),
                log("192.168.1.1"),
                log("192.168.1.3")
        );

        List<String> result = analyzer.getTopIps(logs, 3);

        assertEquals(
                List.of(
                        "192.168.1.1",
                        "192.168.1.2",
                        "192.168.1.3"
                ),
                result
        );
    }

    @Test
    void shouldUseFrequencyBeforeAlphabeticalOrder() {
        List<String> logs = List.of(
                log("192.168.1.1"),
                log("10.0.0.1"),
                log("10.0.0.1")
        );

        List<String> result = analyzer.getTopIps(logs, 2);

        assertEquals(
                List.of("10.0.0.1", "192.168.1.1"),
                result
        );
    }

    @Test
    void shouldReturnAllIpsWhenTopNIsGreaterThanUniqueCount() {
        List<String> logs = List.of(
                log("10.0.0.1"),
                log("10.0.0.1"),
                log("192.168.1.1")
        );

        List<String> result = analyzer.getTopIps(logs, 10);

        assertEquals(
                List.of("10.0.0.1", "192.168.1.1"),
                result
        );
    }

    @Test
    void shouldIgnoreInvalidLinesForTopIps() {
        List<String> logs = List.of(
                log("10.0.0.1"),
                "invalid line",
                log("10.0.0.1"),
                "another invalid line",
                log("192.168.1.1")
        );

        List<String> result = analyzer.getTopIps(logs, 2);

        assertEquals(
                List.of("10.0.0.1", "192.168.1.1"),
                result
        );
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoValidLogs() {
        List<String> logs = List.of(
                "invalid",
                "wrong line"
        );

        List<String> result = analyzer.getTopIps(logs, 3);

        assertTrue(result.isEmpty());
    }

    // перевірка неправильного topN
    @Test
    void shouldThrowExceptionWhenTopNIsZero() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.getTopIps(List.of(), 0)
        );

        assertEquals(
                "topN must be greater than 0",
                exception.getMessage()
        );
    }
    @Test
    void shouldThrowExceptionWhenTopNIsNegative() {
        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.getTopIps(List.of(), -5)
        );
    }

    @Test
    void shouldThrowExceptionWhenLogLinesAreNullForTopIps() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.getTopIps(null, 3)
        );
        assertEquals(
                "logLines cannot be null",
                exception.getMessage()
        );
    }

    private String log(String ip) {
        return ip
                + " ident alice [01/May/2025:07:20:10 +0000] "
                + "\"GET /index.html HTTP/1.1\" 200 9481";
    }
}