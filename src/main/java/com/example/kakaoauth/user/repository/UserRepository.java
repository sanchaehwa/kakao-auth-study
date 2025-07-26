package com.example.kakaoauth.user.repository;

import com.example.kakaoauth.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByKakaoId(Long kakaoId);
    void deleteByKakaoId(Long kakaoId);
}
