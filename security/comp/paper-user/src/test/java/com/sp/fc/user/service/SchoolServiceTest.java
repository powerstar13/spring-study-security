package com.sp.fc.user.service;

import com.sp.fc.user.domain.School;
import com.sp.fc.user.repository.SchoolRepository;
import com.sp.fc.user.service.helper.SchoolTestHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class SchoolServiceTest {

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private SchoolService schoolService;

    private SchoolTestHelper schoolTestHelper;
    School school;

    @BeforeEach
    void before() {
        schoolRepository.deleteAll();

        this.schoolService = new SchoolService(schoolRepository);
        this.schoolTestHelper = new SchoolTestHelper(schoolService);

        this.school = schoolTestHelper.createSchool("테스트 학교", "서울");
    }

    @DisplayName("1. 학교를 생성한다.")
    @Test
    void test_1() {

        List<School> schools = schoolRepository.findAll();

        assertEquals(1, schools.size());
        SchoolTestHelper.assertSchool(schools.get(0), "테스트 학교", "서울");
    }

    @DisplayName("2. 학교 이름을 수정한다.")
    @Test
    void test_2() {

        schoolService.updateName(school.getSchoolId(), "테스트2 학교");

        SchoolTestHelper.assertSchool(schoolRepository.findAll().get(0), "테스트2 학교", "서울");
    }

    @DisplayName("3. 지역 목록을 가져온다.")
    @Test
    void test_3() {

        List<String> cities = schoolService.cities();

        assertEquals(1, cities.size());
        assertEquals("서울", cities.get(0));

        this.school = schoolTestHelper.createSchool("부산 학교", "부산");

        cities = schoolService.cities();

        assertEquals(2, cities.size());
    }

    @DisplayName("4. 지역으로 학교 목록을 가져온다.")
    @Test
    void test_4() {

        List<School> schools = schoolService.findAllByCity("서울");

        assertEquals(1, schools.size());

        this.school = schoolTestHelper.createSchool("서울2 학교", "서울");

        schools = schoolService.findAllByCity("서울");

        assertEquals(2, schools.size());
    }
}