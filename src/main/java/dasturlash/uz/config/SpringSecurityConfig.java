package dasturlash.uz.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    public static String[] openApiList = {"/attach/**",
            "/api/v1/auth/**",
            "/api/v1/attach/upload",
            "/api/v1/attach/open/**",
            "/api/v1/attach/download/**",
            "/api/v1/category/lang",
            "/api/v1/region/lang",
            "/api/v1/article/section/*",
            "/api/v1/article/last-12",
            "/api/v1/article/by-category/**",
            "/api/v1/article/by-region/**",
            "/api/v1/article/detail/*",
            "/api/v1/article/section-small/*",
            "/api/v1/article/most-read/*",
            "/api/v1/article/view-count/*",
            "/api/v1/article/shared-count/*",
            "/api/v1/article/filter",
            "/api/v1/comment/article/*",
            "/api/v1/comment/reply/*",
    };

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // authentication - Foydalanuvchining identifikatsiya qilish.
        // Ya'ni berilgan login va parolli user bor yoki yo'qligini aniqlash.
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(customUserDetailsService);
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // authorization - Foydalanuvchining tizimdagi huquqlarini tekshirish.
        // Ya'ni foydalanuvchi murojat qilayotgan API-larni ishlatishga ruxsati bor yoki yo'qligini tekshirishdir.
        http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
            authorizationManagerRequestMatcherRegistry
                    .requestMatchers(openApiList).permitAll()
                    .requestMatchers("/api/v1/category/admin", "/api/v1/category/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/region/admin", "/api/v1/region/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/profile/admin", "/api/v1/profile/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/section/admin", "/api/v1/section/admin/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/email-history/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/sms-history/**").hasRole("ADMIN")
                    .requestMatchers("/api/v1/moderator/article").hasRole("MODERATOR")
                    .requestMatchers("/api/v1/admin/article").hasRole("ADMIN")
                    .anyRequest()
                    .authenticated();
        }).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.httpBasic(Customizer.withDefaults()); // httpBasic-dan foydanalish uchun u enable qilindi (ishlatmoqchi ekanligimiz yozildi) (yoqib qo'yild).

        http.csrf(AbstractHttpConfigurer::disable); // csrf o'chirilgan

        http.cors(httpSecurityCorsConfigurer -> { // cors konfiguratsiya qilingan
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOriginPatterns(Arrays.asList("*"));
            configuration.setAllowedMethods(Arrays.asList("*"));
            configuration.setAllowedHeaders(Arrays.asList("*"));

            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            httpSecurityCorsConfigurer.configurationSource(source);
        });
        return http.build();
    }


}
