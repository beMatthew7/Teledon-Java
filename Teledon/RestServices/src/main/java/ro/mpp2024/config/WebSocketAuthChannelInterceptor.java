package ro.mpp2024.config;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ro.mpp2024.security.JwtService;
import ro.mpp2024.security.VolunteerDetailsService;

@Component
public class WebSocketAuthChannelInterceptor implements ChannelInterceptor {
    private final JwtService jwtService;
    private final VolunteerDetailsService volunteerDetailsService;

    public WebSocketAuthChannelInterceptor(JwtService jwtService, VolunteerDetailsService volunteerDetailsService) {
        this.jwtService = jwtService;
        this.volunteerDetailsService = volunteerDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new AccessDeniedException("Lipseste token-ul JWT pentru WebSocket.");
            }

            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = volunteerDetailsService.loadUserByUsername(username);

            if (!jwtService.validateToken(token, userDetails)) {
                throw new AccessDeniedException("Token JWT invalid pentru WebSocket.");
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );
            accessor.setUser(authentication);
        }

        return message;
    }
}
