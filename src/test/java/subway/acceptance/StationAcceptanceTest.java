package subway.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import subway.station.presentation.request.CreateStationRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static subway.acceptance.extractableResponse.StationApiExtractableResponse.*;

@DisplayName("지하철역 관련 기능")
@Sql("/truncate.sql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class StationAcceptanceTest {

    /**
     * When 지하철역을 생성하면
     * Then 지하철역이 생성된다
     * Then 지하철역 목록 조회 시 생성한 역을 찾을 수 있다
     */
    @DisplayName("지하철역을 생성한다.")
    @Test
    void 지하철_역을_생성() {
        // when & then
        String 강남역 = "강남역";
        createStation(CreateStationRequest.from(강남역));

        // then
        List<String> stationNames =
                selectStations().jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).containsAnyOf(강남역);
    }

    /**
     * Given 2개의 지하철역을 생성하고
     * When 지하철역 목록을 조회하면
     * Then 2개의 지하철역을 응답 받는다
     */
    @DisplayName("지하철역 목록을 조회한다.")
    @Test
    void 지하철_역_목록을_조회() {
        // given
        String 강남역 = "강남역";
        String 광화문역 = "광화문역";
        createStation(CreateStationRequest.from(강남역));
        createStation(CreateStationRequest.from(광화문역));

        // when
        List<String> stationNames =
                selectStations().jsonPath().getList("stations.name", String.class);

        // then
        assertThat(stationNames).containsAnyOf(강남역, 광화문역);
    }

    /**
     * Given 지하철역을 생성하고
     * When 그 지하철역을 삭제하면
     * Then 그 지하철역 목록 조회 시 생성한 역을 찾을 수 없다
     */
    @DisplayName("지하철역을 삭제한다.")
    @Test
    void 지하철_역을_삭제() {
        // given
        String 강남역 = "강남역";
        Long id = createStation(CreateStationRequest.from(강남역)).jsonPath().getLong("id");

        // when
        deleteStation(id);

        // then
        List<String> stationNames =
                selectStations().jsonPath().getList("stations.name", String.class);
        assertThat(stationNames).doesNotContain(강남역);
    }

}
