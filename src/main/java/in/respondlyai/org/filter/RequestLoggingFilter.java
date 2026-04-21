package in.respondlyai.org.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(1)
@Slf4j
public class RequestLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long start = System.currentTimeMillis();

        String method = request.getMethod();
        String uri = request.getRequestURI();
        String query = request.getQueryString() != null ? "?" + request.getQueryString() : "";
        String remoteAddr = request.getRemoteAddr();

        log.info("--> {} {}{} from [{}]", method, uri, query, remoteAddr);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.currentTimeMillis() - start;
            int status = response.getStatus();

            if (status >= 500) {
                log.error("<-- {} {} {} ({}ms)", method, uri, status, duration);
            } else if (status >= 400) {
                log.warn("<-- {} {} {} ({}ms)", method, uri, status, duration);
            } else {
                log.info("<-- {} {} {} ({}ms)", method, uri, status, duration);
            }
        }
    }
}
