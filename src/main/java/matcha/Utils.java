package matcha;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class Utils {
    public int convertTicksToInt(double doubleTicks) {
        return Math.toIntExact(Math.round(doubleTicks * 10000));
    }
}
