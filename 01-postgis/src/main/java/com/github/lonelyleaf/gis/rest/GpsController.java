package com.github.lonelyleaf.gis.rest;

import com.github.lonelyleaf.gis.dto.GpsDto;
import com.github.lonelyleaf.gis.dto.SimplePoint;
import com.github.lonelyleaf.gis.entity.GpsEntity;
import com.github.lonelyleaf.gis.mapper.GpsMapper;
import com.github.lonelyleaf.gis.service.GpsService;
import com.google.common.base.Strings;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/gps")
public class GpsController {

    private final GpsService gpsService;
    private final GpsMapper gpsMapper;

    public GpsController(GpsService gpsService, GpsMapper gpsMapper) {
        this.gpsService = gpsService;
        this.gpsMapper = gpsMapper;
    }

    @GetMapping("/history")
    public List<GpsDto> history(
            @RequestParam(value = "devId", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String devId,
            @RequestParam(value = "bTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date bTime,
            @RequestParam(value = "eTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date eTime) {
        return gpsService.history(devId, bTime, eTime).stream()
                .map(gpsMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/history/simple")
    public List<SimplePoint> simpleHistory(
            @RequestParam(value = "devId", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String devId,
            @RequestParam(value = "bTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date bTime,
            @RequestParam(value = "eTime", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date eTime) {
        List<GpsEntity> history = gpsService.history(devId, bTime, eTime);
        return history.stream()
                .map(gpsEntity -> new SimplePoint(gpsEntity.getLocation()))
                .collect(Collectors.toList());
    }

    @PostMapping
    public String simpleHistory(@RequestBody GpsDto gpsDto) {
        if (gpsDto.getTime() == null) {
            gpsDto.setTime(new Date());
        }
        if (Strings.isNullOrEmpty(gpsDto.getDevId())) {
            throw new IllegalArgumentException("devId不能为空");
        }

        GpsEntity entity = gpsMapper.toEntity(gpsDto);
        gpsService.save(entity);
        return "ok";
    }

}
