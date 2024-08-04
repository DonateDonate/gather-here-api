package gather.here.api.presentation.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class SecurityTestController {

    @GetMapping("/empty/request")
    public ResponseEntity<Object> getMappingTest(){
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
