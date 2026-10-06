package ua.kpi.comsys.collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogAnalyzer {

    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^(\\S+)\\s+\\S+\\s+\\S+\\s+\\[[^\\]]+\\]\\s+\"[^\"]*\"\\s+\\d{3}\\s+(?:\\d+|-)$"
    );

    public Set<String> getUniqueIps(List<String> logLines) {
        if (logLines == null) {
            throw new IllegalArgumentException("logLines cannot be null");
        }

        Set<String> uniqueIps = new HashSet<>();

        for (String line : logLines) {
            String ip = extractIp(line);

            if (ip != null) {
                uniqueIps.add(ip);
            }
        }
        return uniqueIps;
    }

    public List<String> getTopIps(List<String> logLines, int topN) {
        if (logLines == null) {
            throw new IllegalArgumentException("logLines cannot be null");
        }
        if (topN <= 0) {
            throw new IllegalArgumentException("topN must be greater than 0");
        }
        Map<String, Integer> ipCount = new HashMap<>();

        // рахуємо кількість входжень кожного ip
        for (String line : logLines) {
            String ip = extractIp(line);

            if (ip != null) {
                ipCount.put(ip, ipCount.getOrDefault(ip, 0) + 1);
            }
        }

        List<String> ips = new ArrayList<>(ipCount.keySet());

        // спочатку сортуємо за частотою, потім за ip
        ips.sort((first, second) -> {
            int frequencyCompare = Integer.compare(
                    ipCount.get(second),
                    ipCount.get(first)
            );

            if (frequencyCompare != 0) {
                return frequencyCompare;
            }
            return first.compareTo(second);
        });

        if (ips.size() > topN) {
            return new ArrayList<>(ips.subList(0, topN));
        }

        return ips;
    }
    private String extractIp(String line) {
        if (line == null) {
            return null;
        }
        Matcher matcher = LOG_PATTERN.matcher(line);

        // неправильні рядки просто ігноруємо
        if (!matcher.matches()) {
            return null;
        }

        return matcher.group(1);
    }
}