package vn.iotstar.filters;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import vn.iotstar.entity.User;
import vn.iotstar.utils.JwtTokenUtil;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            if(isBypassToken(request)) {
                filterChain.doFilter(request, response); //enable bypass
                return;
            }
            final String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }
            final String token = authHeader.substring(7);
            final String phoneNumber = jwtTokenUtil.extractPhoneNumber(token);
            if (phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User userDetails = (User) userDetailsService.loadUserByUsername(phoneNumber);
                if(jwtTokenUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null,
                                    userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            filterChain.doFilter(request, response); //enable bypass
        }catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

    private boolean isBypassToken(@NonNull HttpServletRequest request) {
        final List<Pair<String, String>> bypassTokens = Arrays.asList(
                Pair.of("users/register", "GET"),
                Pair.of("users/login", "GET"),
                Pair.of("users/login", "POST"),
                Pair.of("users/register", "POST"),
                Pair.of("admin/home2", "GET"),
                Pair.of("users/forgot-password", "GET"),
                Pair.of("users/forgot-password", "POST"),
                Pair.of("users/reset-password", "GET"),
                Pair.of("users/reset-password", "POST"),
                Pair.of("users/verify-otp", "POST"),
                Pair.of("css/dep.css", "GET"),      // Thêm tài nguyên CSS
                Pair.of("js/script.js", "GET"),       // Thêm tài nguyên JS
                Pair.of("images/", "GET"),   // Thêm tài nguyên hình ảnh
                Pair.of("fonts/", "GET"),     // Thêm tài nguyên font (nếu có)
                Pair.of("css/qr.jpg", "GET"),
                Pair.of("css/qr.jpg", "POST"),
                Pair.of("categories", "GET"),
                Pair.of("categories", "POST"),
                Pair.of("cart", "POST"),
                Pair.of("cart", "GET"),
                Pair.of("category", "GET"),
                Pair.of("category", "POST"),
                Pair.of("product", "GET"),
                Pair.of("product", "POST"),
                Pair.of("address", "GET"),
                Pair.of("address", "POST"),
                Pair.of("address", "DELETE"),
                Pair.of("api", "GET"),
                Pair.of("api", "POST"),
                Pair.of("order", "GET"),
                Pair.of("order", "POST"),
                Pair.of("voucher", "GET"),
                Pair.of("voucher", "POST"),
                Pair.of("vendor", "GET"),
                Pair.of("vendor", "POST"),
                Pair.of("vendor", "DELETE"),
                Pair.of("like", "GET"),
                Pair.of("like", "POST"),
                Pair.of("contact", "GET"),
                Pair.of("contact", "POST"),
                Pair.of("admin", "GET"),
                Pair.of("admin", "POST"),
                Pair.of("css/style1.css", "GET"),
                Pair.of("css/AdminLTE.css", "GET"),
                Pair.of("css/bootstrap.min1.css", "GET"),
                Pair.of("css/font-awesome.min.css", "GET"),
                Pair.of("css/jquery-ui.css", "GET"),
                Pair.of("css/nestable.css", "GET"),
                Pair.of("css/_all-skins.min.css", "GET"),
                Pair.of("images/user2-160x160.jpg", "GET"),
                Pair.of("/images/user2-160x160.jpg", "GET"),
                Pair.of("js/jquery.min.js", "GET"),
                Pair.of("js/jquery-ui.js", "GET"),
                Pair.of("js/bootstrap.min.js", "GET"),
                Pair.of("js/adminlte.min.js", "GET"),
                Pair.of("js/dashboard.js", "GET"),
                Pair.of("js/function.js", "GET"),
                Pair.of("fonts/fontawesome-webfont.ttf", "GET"),
                Pair.of("fonts/fontawesome-webfont.woff", "GET"),
                Pair.of("fonts/fontawesome-webfont.woff2", "GET"),
                Pair.of("shipper", "GET"),
                Pair.of("shipper", "POST"),
                Pair.of("profile", "GET"),
                Pair.of("profile", "POST"),
                Pair.of("css/style.css", "GET")
        );
        for(Pair<String, String> bypassToken: bypassTokens) {
            if (request.getServletPath().contains(bypassToken.getFirst()) &&
                    request.getMethod().equals(bypassToken.getSecond())) {
                return true;
            }
        }
        return false;
    }

}

