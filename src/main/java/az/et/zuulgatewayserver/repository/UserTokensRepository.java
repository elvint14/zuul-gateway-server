package az.et.zuulgatewayserver.repository;

import az.et.zuulgatewayserver.model.UserEntity;
import az.et.zuulgatewayserver.model.UserTokensEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTokensRepository extends JpaRepository<UserTokensEntity, Long> {

    Optional<UserTokensEntity> findByUserId(Long userId);

    boolean existsUserTokensEntityByAccessTokenAndUser(String token, UserEntity user);

    List<UserTokensEntity> findAllByUserId(Long userId);

}
