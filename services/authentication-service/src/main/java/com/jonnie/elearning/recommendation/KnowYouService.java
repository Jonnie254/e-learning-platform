package com.jonnie.elearning.recommendation;


import com.jonnie.elearning.exceptions.UserNotFoundException;
import com.jonnie.elearning.recommendation.requests.KnowYouRequest;
import com.jonnie.elearning.recommendation.responses.KnowYouResponse;
import com.jonnie.elearning.repositories.KnowYouRepository;
import com.jonnie.elearning.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KnowYouService {
    private final KnowYouRepository knowYouRepository;
    private final KnowYouMapper knowYouMapper;
    private final UserRepository userRepository;

    public void createKnowYou(String userId, @Valid KnowYouRequest knowYouRequest) {
        boolean userExists = userRepository.existsById(userId);
        if (!userExists) {
            throw new UserNotFoundException("User with ID " + userId + " not found");
        }
        KnowYou knowYou = knowYouMapper.mapToKnowYou(knowYouRequest);
        knowYou.setUserId(userId);
        knowYouRepository.save(knowYou);
    }
    public KnowYouResponse getUserKnowYou(String userId) {
        KnowYou knowYou = knowYouRepository.findByUserId(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));
        return knowYouMapper.mapToKnowYouResponse(knowYou);
    }

    public boolean checkUserKnowYou(String userId) {
        return knowYouRepository.findByUserId(userId).isPresent();
    }
}
