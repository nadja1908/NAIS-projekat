package com.nais.microservice_analytics_java.configuration;

import com.influxdb.client.DeleteApi;
import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.exceptions.InfluxException;
import com.nais.microservice_analytics_java.model.AttendanceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class AttendanceDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AttendanceDataInitializer.class);

    @Autowired
    private InfluxDBClient influxDBClient;

    @Value("${spring.influx.bucket}")
    private String bucket;

    @Value("${spring.influx.org}")
    private String org;

    private record ScheduleEvent(String courseCode, String roomId, String activityType, int maxStudents) {}

    @Override
    public void run(String... args) throws Exception {

        clearPreviousData();

        logger.info("Starting InfluxDB attendance data seeding...");

        // Definicija skupa osnovnih kurseva/termina/sala
        List<ScheduleEvent> scheduleEvents = getScheduleEvents();

        List<AttendanceRecord> attendanceRecords = new ArrayList<>();
        Random random = new Random();

        // Početak simulacije: pre 6 meseci, počinjemo od 8:00 AM
        Instant timelineCurrentTime = ZonedDateTime.now(ZoneId.of("UTC"))
                .minusMonths(6)
                .withHour(8)
                .withMinute(0)
                .withSecond(0)
                .toInstant();

        // Generiši 10.000 pojedinačnih tačaka prisustva tokom simuliranog perioda
        for (int i = 0; i < 10000; i++) {

            // Uzmi nasumični događaj iz rasporeda
            ScheduleEvent event = scheduleEvents.get(random.nextInt(scheduleEvents.size()));

            // Prosečna popunjenost: 60% do 90%
            double attendanceRate = 0.6 + random.nextDouble() * 0.3;

            // Neka mali procenat događaja simulira kritično NISKU posećenost (< 30%)
            if (random.nextDouble() < 0.05) { // 5% šanse za vrlo nisko prisustvo
                attendanceRate = 0.1 + random.nextDouble() * 0.2;
            }

            int actualAttendance = (int) (event.maxStudents * attendanceRate);
            if (actualAttendance < 1) actualAttendance = 1;

            // Generiši zapis za svakog prisutnog studenta u ovom terminu
            generateAttendanceForEvent(attendanceRecords, event, timelineCurrentTime, actualAttendance, random);

            // Pomakni vreme za sledeći događaj nasumično (npr. za 5 do 30 minuta)
            timelineCurrentTime = timelineCurrentTime.plus(random.nextInt(25) + 5, ChronoUnit.MINUTES);

            // Ako je vreme prošlo 20:00, prebaci na sledeći dan (simulacija radnih dana)
            if (timelineCurrentTime.atZone(ZoneId.of("UTC")).getHour() >= 20) {
                timelineCurrentTime = timelineCurrentTime.plus(1, ChronoUnit.DAYS).with(ChronoUnit.HOURS.addTo(timelineCurrentTime, 8));
            }
        }

        logger.info("Generated {} AttendanceRecord records.", attendanceRecords.size());

        // Upis podataka
        writeDataToInflux(attendanceRecords);

        logger.info("InfluxDB data seeding finished successfully.");
    }

    /**
     * Definiše skup fiktivnih kurseva, sala i kapaciteta.
     * Ovi podaci služe kao baza za generisanje test scenarija (npr. prevelika/premala sala).
     */
    private List<ScheduleEvent> getScheduleEvents() {
        return List.of(
                // Visoko opterećenje - HALL_1 (Kapacitet 250)
                new ScheduleEvent("UVOD-101", "HALL_1", "PREDAVANJE", 250),
                new ScheduleEvent("UVOD-101", "A101", "VEZBE", 40),

                // Normalno opterećenje - B205 (Kapacitet 100)
                new ScheduleEvent("OOP-202", "B205", "PREDAVANJE", 100),
                new ScheduleEvent("MRS-305", "B205", "PREDAVANJE", 100),

                // Niska popunjenost - A101 (Kapacitet 40)
                new ScheduleEvent("EUP-310", "A101", "PREDAVANJE", 40),

                // Laboratorije - LAB_3 (Kapacitet 20)
                new ScheduleEvent("LAB-401", "LAB_3", "LABORATORIJA", 20),
                new ScheduleEvent("LAB-402", "LAB_3", "LABORATORIJA", 20)
        );
    }

    /**
     * Generiše pojedinačne zapise (tačke) prisustva za svaki termin/događaj.
     */
    private void generateAttendanceForEvent(List<AttendanceRecord> records, ScheduleEvent event, Instant startTime, int actualAttendance, Random random) {

        // Generiše fiktivne ID-jeve studenata
        List<String> studentIds = new ArrayList<>();
        for(int i = 1; i <= event.maxStudents; i++) {
            studentIds.add("S_" + i);
        }

        // Nasumično bira podskup studenata koji su prisutni
        Collections.shuffle(studentIds, random);
        List<String> presentStudents = studentIds.subList(0, actualAttendance);

        // Vreme početka termina sa malom varijacijom
        Instant eventTime = startTime.plus(random.nextInt(60), ChronoUnit.SECONDS);

        for (String studentId : presentStudents) {
            AttendanceRecord record = new AttendanceRecord();
            record.setStudentId(studentId);
            record.setCourseCode(event.courseCode());
            record.setActivityType(event.activityType());
            record.setRoomId(event.roomId());
            record.setPresentCount(1L);

            // Simulira vreme ulaska (do 5 minuta nakon početka termina)
            Instant recordTime = eventTime.plusSeconds(random.nextInt(300));
            record.setTime(recordTime);

            records.add(record);
        }
    }

    private void writeDataToInflux(List<AttendanceRecord> records) {
        try (WriteApi writeApi = influxDBClient.getWriteApi()) {

            logger.info("Writing {} AttendanceRecord records to InfluxDB bucket...", records.size());

            // Pišemo pomoću Point objekta, što je pouzdanije
            for (AttendanceRecord record : records) {
                Point point = Point
                        .measurement("student_presence")
                        .addTag("studentId", record.getStudentId())
                        .addTag("courseCode", record.getCourseCode())
                        .addTag("activityType", record.getActivityType())
                        .addTag("roomId", record.getRoomId())
                        .addField("presentCount", record.getPresentCount())
                        .time(record.getTime(), WritePrecision.NS);

                writeApi.writePoint(point);
            }

            writeApi.flush();
            logger.info("Data successfully flushed to InfluxDB.");

        } catch (InfluxException e) {
            logger.error("Failed to write data to InfluxDB: {}", e.getMessage(), e);
        }
    }

    private void clearPreviousData() {
        logger.info("Clearing previous data from bucket: {}", bucket);
        DeleteApi deleteApi = influxDBClient.getDeleteApi();

        // Brišemo sve podatke iz poslednje dve godine
        OffsetDateTime start = OffsetDateTime.parse("2023-01-01T00:00:00Z");
        OffsetDateTime stop = OffsetDateTime.now().plusMonths(1);

        try {
            String predicate = "_measurement=\"student_presence\"";
            deleteApi.delete(start, stop, predicate, bucket, org);
            logger.info("Cleared all data from 'student_presence' measurement.");
        } catch (InfluxException e) {
            logger.error("Failed to clear previous data from InfluxDB: {}", e.getMessage(), e);
        }
    }
}
