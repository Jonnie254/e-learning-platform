package com.jonnie.elearning.services;

import lombok.Builder;

@Builder
public record AuthenticationResponse(
        String token
) {
}
