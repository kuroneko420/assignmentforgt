import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.assignmentgt.demo.DemoApplication;
import com.assignmentgt.demo.model.User;
import com.assignmentgt.demo.repository.UsersRepository;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsersRepository usersRepository;

    @Test
    public void testGetUsersWithLimitAndOffset() throws Exception {
        // Given
        List<User> allUsers = new ArrayList<>();
        allUsers.add(new User("Alice", 500.0));
        allUsers.add(new User("Bob", 1000.0));
        allUsers.add(new User("Carol", 2000.0));
        allUsers.add(new User("David", 3000.0));

        List<User> expectedUsers = allUsers.subList(1, 3); // Bob and Carol

        // Set up the mock behavior of the repository
        when(usersRepository.findBySalaryRangeAndSort(any(BigDecimal.class), any(BigDecimal.class), anyString(),
                anyInt(), anyInt()))
                .thenReturn(expectedUsers);

        // When & Then
        mockMvc.perform(get("/api/users")
                .param("offset", "1")
                .param("limit", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results", hasSize(2)))
                .andExpect(jsonPath("$.results[0].name", is("Bob")))
                .andExpect(jsonPath("$.results[0].salary", is(1000.0)))
                .andExpect(jsonPath("$.results[1].name", is("Carol")))
                .andExpect(jsonPath("$.results[1].salary", is(2000.0)));
    }

    @Test
    public void testInvalidSortParam() throws Exception {
        mockMvc.perform(get("/api/users")
                .param("sort", "INVALID"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid sort parameter. Allowed values are 'NAME' or 'SALARY'."));
    }

    @Test
    public void testUploadValidCSV() throws Exception {
        String validCsv = "NAME,SALARY\nAlice,3000\nBob,3200";
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", validCsv.getBytes());
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload").file(file))
                .andExpect(request().asyncStarted())
                .andDo(MvcResult::getAsyncResult)
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("1"));
    }

    @Test
    public void testUploadCSVWithInvalidHeader() throws Exception {
        String invalidHeaderCsv = "WrongName,WrongSalary\nAlice,3000\nBob,3200";
        MockMultipartFile file = new MockMultipartFile("file", "invalidHeader.csv", "text/csv",
                invalidHeaderCsv.getBytes());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload").file(file))
                .andExpect(request().asyncStarted())
                .andDo(MvcResult::getAsyncResult)
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid CSV format: header row is incorrect"));
    }

    @Test
    public void testUploadCSVWithInvalidRows() throws Exception {
        String csvWithInvalidRows = "NAME,SALARY\nAlice,3000\nBob,3200\nCharlie,invalid";
        MockMultipartFile file = new MockMultipartFile("file", "invalidRows.csv", "text/csv",
                csvWithInvalidRows.getBytes());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/upload").file(file))
                .andExpect(request().asyncStarted())
                .andDo(MvcResult::getAsyncResult)
                .andReturn();

        mockMvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid CSV format: salary cannot be parsed"));
    }

}