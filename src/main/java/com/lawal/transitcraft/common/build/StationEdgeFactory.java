package com.lawal.transitcraft.common.build;

import com.lawal.transitcraft.infrastructure.block.Block;
import com.lawal.transitcraft.infrastructure.catalog.CurbCatalog;
import com.lawal.transitcraft.infrastructure.catalog.RoadCatalog;
import com.lawal.transitcraft.infrastructure.catalog.StationEdgeCatalog;
import com.lawal.transitcraft.infrastructure.curb.Curb;
import com.lawal.transitcraft.common.Direction;
import com.lawal.transitcraft.infrastructure.road.Road;
import com.lawal.transitcraft.infrastructure.station.Station;
import com.lawal.transitcraft.infrastructure.station.StationEdge;

import java.util.EnumSet;
import java.util.concurrent.atomic.AtomicLong;

public enum StationEdgeFactory {

    INSTANCE;

    private AtomicLong id = new AtomicLong(0);

    public void run() {
        buildCurbEdges();
        buildCycleEdges();
    }

    private void buildCurbEdges() {
        EnumSet<Direction> curbDirections = EnumSet.of(
            Direction.NORTH,
            Direction.EAST,
            Direction.SOUTH,
            Direction.WEST
        );

        for (Direction curbDirection : curbDirections) {
            for (Curb curb : CurbCatalog.INSTANCE.filterByOrientation(curbDirection)) { curbEdgeBuildHelper(curb); }
        }
    }

    private void curbEdgeBuildHelper(Curb curb) {
        if (curb == null || curb.getStations().isEmpty()) return;

        Station previousStation = curb.getStations().get(0);
        if (previousStation == null) return;

        for (Station station : curb.getStations()) {
            Block block = station.getBlock();
            int distance = curb.getBlocks().indexOf(block) - curb.getBlocks().indexOf(previousStation.getBlock());
            if (distance > 0) {
                StationEdge edge = new StationEdge(
                    id.incrementAndGet(),
                    previousStation,
                    station,
                    distance,
                    0,
                    0
                );
                StationEdgeCatalog.INSTANCE.addEdge(edge);
            }
            previousStation = station;
        }
    }

    private void buildCycleEdges() {
        for (Road road : RoadCatalog.INSTANCE.getCatalog()) {
            if (road.getLeftCurb() == null || road.getRightCurb() == null) continue;

            cycleEdgeBuildHelper(road.getLeftCurb(), road.getRightCurb());
            cycleEdgeBuildHelper(road.getRightCurb(), road.getLeftCurb());
        }
    }

    private void cycleEdgeBuildHelper(Curb startingCurb, Curb endingCurb) {
        if (startingCurb == null || endingCurb == null ) return;
        if (startingCurb.equals(endingCurb)) return;
        if (startingCurb.getStations().isEmpty() || endingCurb.getStations().isEmpty()) return;

        Station startingStation = startingCurb.getfirstStation();
        Station endingStation = endingCurb.getLastStation();

        if (startingStation == null || endingStation == null) return;

        int startingBlockArrayIndex = startingCurb.getBlocks().indexOf(startingStation.getBlock());
        int endingBlockArrayIndex = endingCurb.getBlocks().indexOf(endingStation.getBlock());

        int startingIndex = startingCurb.getBlocks().size() - startingCurb.getBlocks().indexOf(startingStation.getBlock()) - 1;
        int endingIndex = endingCurb.getBlocks().size() - endingCurb.getBlocks().indexOf(endingStation.getBlock()) - 1;

        int roadDistance = Math.abs(endingCurb.getRoad().getId().intValue() - startingCurb.getRoad().getId().intValue());
        int curbDistance = Math.abs(endingCurb.getId().intValue() - startingCurb.getId().intValue());
        int blocKDistance = Math.abs(endingIndex - startingIndex);

        int distance = roadDistance + blocKDistance + curbDistance;

        StationEdge edge = new StationEdge(
            id.incrementAndGet(),
            startingStation, endingStation,
            distance,
            0,
            0
        );

        StationEdgeCatalog.INSTANCE.getCatalog().add(edge);
//        System.out.println("cycleEdge:" + edge.getId() + " catalog size:" + StationEdgeCatalog.INSTANCE.size());
    }
}