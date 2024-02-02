package subway.line.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import subway.line.domain.Line;
import subway.line.domain.LineRepository;
import subway.line.presentaion.request.CreateLineRequest;
import subway.line.presentaion.request.UpdateLineRequest;
import subway.line.presentaion.response.LineResponse;
import subway.station.domain.Station;
import subway.station.domain.StationRepository;
import subway.station.presentaion.response.StationResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LineService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public LineService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    @Transactional
    public LineResponse saveLine(CreateLineRequest createLineRequest) {
        Station upStation = stationRepository.getById(createLineRequest.getUpStationId());
        Station downStation = stationRepository.getById(createLineRequest.getDownStationId());

        Line line = lineRepository.save(
                new Line(
                        createLineRequest.getName(), createLineRequest.getColor(), upStation, downStation, createLineRequest.getDistance()
                )
        );

        return createLineResponse(line);
    }

    public List<LineResponse> findAllLines() {
        return lineRepository.findAll().stream()
                .map(this::createLineResponse)
                .collect(Collectors.toList());
    }

    public LineResponse findLine(Long id) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 지하철 노선입니다."));

        return createLineResponse(line);
    }

    @Transactional
    public LineResponse updateLine(Long id, UpdateLineRequest updateLineRequest) {
        Line line = lineRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "없는 지하철 노선입니다."));

        line.updateLine(updateLineRequest.getColor(), updateLineRequest.getDistance());

        return createLineResponse(line);
    }

    @Transactional
    public void deleteLine(final Long id) {
        lineRepository.deleteById(id);
//        final Line line = lineRepository.getById(id);
//        line.delete();
    }

    private LineResponse createLineResponse(Line line) {
        return new LineResponse(
                line.getId(),
                line.getName(),
                line.getColor(),
                List.of(
                        new StationResponse(line.getUpStation().getId(), line.getUpStation().getName()),
                        new StationResponse(line.getDownStation().getId(), line.getDownStation().getName())
                ),
                line.getDistance()
        );
    }

}
