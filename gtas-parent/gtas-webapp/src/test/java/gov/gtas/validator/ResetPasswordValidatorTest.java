package gov.gtas.validator;

import dtos.PasswordResetDto;
import gov.gtas.model.User;
import gov.gtas.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import static gov.gtas.validator.ResetPasswordValidator.INVALID_CREDENTIALS_REQUEST_ERROR;
import static gov.gtas.validator.ResetPasswordValidator.MATCHING_PASSWORDS_ERROR;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ResetPasswordValidatorTest {

    @InjectMocks
    private ResetPasswordValidator resetPasswordValidator;

    @Mock
    private UserRepository userRepository;

    @Test
    public void verify_happy_path() {
        PasswordResetDto dto = new PasswordResetDto();
        dto.setResetToken("reset token");
        dto.setUsername("user");
        dto.setPassword("pAss1234!");
        dto.setPasswordConfirm("pAss1234!");

        User user = new User();
        user.setResetToken("reset token");
        user.setUserId("user");
        user.setPassword("pAss1234!!");

        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "dto");
        when(userRepository.findOne(anyString())).thenReturn(user);
        resetPasswordValidator.validate(dto, bindingResult);
        assertTrue(isEmpty(bindingResult.getAllErrors()));
    }


    @Test
    public void verify_null_user_generates_error() {
        PasswordResetDto dto = new PasswordResetDto();
        dto.setResetToken("rest token");
        dto.setUsername("user");
        dto.setPassword("pAss1234!");
        dto.setPasswordConfirm("pAss1234!");

        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "dto");
        when(userRepository.findOne(anyString())).thenReturn(null);
        resetPasswordValidator.validate(dto, bindingResult);
        assertEquals(INVALID_CREDENTIALS_REQUEST_ERROR, bindingResult.getAllErrors().get(0).getDefaultMessage());
        assertEquals(1, bindingResult.getAllErrors().size());
    }

    @Test
    public void verify_invalid_reset_token_generates_error() {
        PasswordResetDto dto = new PasswordResetDto();
        dto.setResetToken("invalid token");
        dto.setUsername("user");
        dto.setPassword("pAss1234!");
        dto.setPasswordConfirm("pAss1234!");

        User user = new User();
        user.setResetToken("reset token");
        user.setUserId("user");
        user.setPassword("pAssa1234!");

        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "dto");
        when(userRepository.findOne(anyString())).thenReturn(user);
        resetPasswordValidator.validate(dto, bindingResult);
        assertEquals(INVALID_CREDENTIALS_REQUEST_ERROR, bindingResult.getAllErrors().get(0).getDefaultMessage());
        assertEquals(1, bindingResult.getAllErrors().size());
    }

    @Test
    public void verify_reused_password_generates_error() {
        PasswordResetDto dto = new PasswordResetDto();
        dto.setResetToken("reset token");
        dto.setUsername("user");
        dto.setPassword("pAss1234!");
        dto.setPasswordConfirm("pAss1234!");

        User user = new User();
        user.setResetToken("reset token");
        user.setUserId("user");
        user.setPassword("pAss1234!");

        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "dto");
        when(userRepository.findOne(anyString())).thenReturn(user);
        resetPasswordValidator.validate(dto, bindingResult);
        assertEquals(INVALID_CREDENTIALS_REQUEST_ERROR, bindingResult.getAllErrors().get(0).getDefaultMessage());
        assertEquals(1, bindingResult.getAllErrors().size());
    }

    @Test
    public void verify_password_with_incorrect_confirmationPassword_generates_error() {
        PasswordResetDto dto = new PasswordResetDto();
        dto.setResetToken("reset token");
        dto.setUsername("user");
        dto.setPassword("pAss1234!");
        dto.setPasswordConfirm("pAss1234!NotMatch");

        User user = new User();
        user.setResetToken("reset token");
        user.setUserId("user");
        user.setPassword("pAss1234!!");

        BindingResult bindingResult = new BeanPropertyBindingResult(dto, "dto");
        when(userRepository.findOne(anyString())).thenReturn(user);
        resetPasswordValidator.validate(dto, bindingResult);
        assertEquals(MATCHING_PASSWORDS_ERROR, bindingResult.getAllErrors().get(0).getDefaultMessage());
        assertEquals(1, bindingResult.getAllErrors().size());
    }

}