package com.technokratos.service.util;

import com.technokratos.dto.kafkaMessage.SeatUpdateStatus;
import com.technokratos.dto.kafkaMessage.UpdateSeatMessage;
import com.technokratos.model.Hall;
import com.technokratos.model.Location;
import com.technokratos.model.Row;
import com.technokratos.model.Seat;

import java.util.*;

public class SeatDiffUtil {

    public static List<UpdateSeatMessage> compareLocations(Location oldLocation, Location newLocation) {
        List<UpdateSeatMessage> updates = new ArrayList<>();

        Map<String, Hall> oldHallsMap = mapHallsByName(oldLocation.getHalls());
        Map<String, Hall> newHallsMap = mapHallsByName(newLocation.getHalls());

        for (Map.Entry<String, Hall> oldHallEntry : oldHallsMap.entrySet()) {
            String hallName = oldHallEntry.getKey();
            Hall oldHall = oldHallEntry.getValue();
            Hall newHall = newHallsMap.get(hallName);

            Map<Integer, Row> oldRowsMap = mapRowsByNum(oldHall.getRows());
            Map<Integer, Row> newRowsMap = newHall != null ? mapRowsByNum(newHall.getRows()) : Collections.emptyMap();

            for (Map.Entry<Integer, Row> oldRowEntry : oldRowsMap.entrySet()) {
                int rowNum = oldRowEntry.getKey();
                Row oldRow = oldRowEntry.getValue();
                Row newRow = newRowsMap.get(rowNum);

                Set<Integer> oldSeats = mapSeatNums(oldRow.getSeats());
                Set<Integer> newSeats = newRow != null ? mapSeatNums(newRow.getSeats()) : Collections.emptySet();

                for (Integer seatNum : oldSeats) {
                    if (!newSeats.contains(seatNum)) {
                        updates.add(new UpdateSeatMessage(
                                oldLocation.getId(),
                                hallName,
                                rowNum,
                                seatNum,
                                SeatUpdateStatus.REMOVED
                        ));
                    }
                }
            }
        }

        for (Map.Entry<String, Hall> newHallEntry : newHallsMap.entrySet()) {
            String hallName = newHallEntry.getKey();
            Hall newHall = newHallEntry.getValue();
            Hall oldHall = oldHallsMap.get(hallName);

            Map<Integer, Row> newRowsMap = mapRowsByNum(newHall.getRows());
            Map<Integer, Row> oldRowsMap = oldHall != null ? mapRowsByNum(oldHall.getRows()) : Collections.emptyMap();

            for (Map.Entry<Integer, Row> newRowEntry : newRowsMap.entrySet()) {
                int rowNum = newRowEntry.getKey();
                Row newRow = newRowEntry.getValue();
                Row oldRow = oldRowsMap.get(rowNum);

                Set<Integer> newSeats = mapSeatNums(newRow.getSeats());
                Set<Integer> oldSeats = oldRow != null ? mapSeatNums(oldRow.getSeats()) : Collections.emptySet();

                for (Integer seatNum : newSeats) {
                    if (!oldSeats.contains(seatNum)) {
                        updates.add(new UpdateSeatMessage(
                                newLocation.getId(),
                                hallName,
                                rowNum,
                                seatNum,
                                SeatUpdateStatus.CREATED
                        ));
                    }
                }
            }
        }

        return updates;
    }

    private static Map<String, Hall> mapHallsByName(List<Hall> halls) {
        Map<String, Hall> map = new HashMap<>();
        if (halls != null) {
            for (Hall hall : halls) {
                map.put(hall.getName(), hall);
            }
        }
        return map;
    }

    private static Map<Integer, Row> mapRowsByNum(List<Row> rows) {
        Map<Integer, Row> map = new HashMap<>();
        if (rows != null) {
            for (Row row : rows) {
                map.put(row.getNum(), row);
            }
        }
        return map;
    }

    private static Set<Integer> mapSeatNums(List<Seat> seats) {
        Set<Integer> set = new HashSet<>();
        if (seats != null) {
            for (Seat seat : seats) {
                set.add(seat.getNum());
            }
        }
        return set;
    }
}
