package pl.bartlomiej.securecapita.user.dto;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import pl.bartlomiej.securecapita.user.User;

@Component
public class UserDtoMapper {
    public static UserReadDto mapToReadDto(User user) {
        UserReadDto userReadDto = new UserReadDto();
        BeanUtils.copyProperties(user, userReadDto);
        return userReadDto;
    }

    public static UserSecurityDto mapToSecurityDto(User user) {
        return new UserSecurityDto(user);
    }

    public static User mapFromCreateDto(UserCreateDto userCreateDto) {
        User user = new User();
        BeanUtils.copyProperties(userCreateDto, user);
        return user;
    }

}
