package com.jonnie.elearning.kafka.cart;

import lombok.*;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    public String id;
    public String firstName;
    public String lastName;
    public String email;
}
