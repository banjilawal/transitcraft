package com.lawal.transitcraft.infrastructure.catalog;

import com.lawal.transitcraft.infrastructure.avenue.Avenue;
import com.lawal.transitcraft.infrastructure.curb.Curb;
import com.lawal.transitcraft.infrastructure.station.StationEdge;
import com.lawal.transitcraft.infrastructure.station.Station;
import com.lawal.transitcraft.infrastructure.street.Street;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum StationEdgeCatalog {
    INSTANCE;

    private final List<StationEdge> catalog;

    StationEdgeCatalog () { catalog = new ArrayList<>(); }

//    public List<StationEdge> getCatalog() {
//        return List.copyOf(catalog);
//    }

    public int size() { return catalog.size(); }

    public void addEdge(StationEdge stationEdge) {
        if (stationEdge == null) {
            System.out.println("StationEdgeCatalog.addEdge(): null stationEdge");
            return;
        }
        if (catalog.contains(stationEdge)) {
            System.out.println("StationEdgeCatalog.addEdge() already contains " + stationEdge.getId() + " catalog size:" + catalog.size());
            return;
        }
//        System.out.println("StationEdgeCatalog.add():" + stationEdge);
        catalog.add(stationEdge);
    }

    public StationEdge findById(Long id) {
        if (id == null) return null;

        for (StationEdge stationEdge : catalog) { if (stationEdge.getId().equals(id)) return stationEdge; }
        return null;
    }

    public List<StationEdge> filterByAvenue(Avenue avenue) {
        List<StationEdge> matches = new ArrayList<>();
        if (avenue == null) return matches;

        for (StationEdge stationEdge : catalog) {
            Avenue headAvenue = stationEdge.getHead().getBlock().getAvenue();
            Avenue tailAvenue = stationEdge.getTail().getBlock().getAvenue();

            if (headAvenue == null || tailAvenue == null) continue;
            if (headAvenue.equals(avenue) || tailAvenue.equals(avenue) && !matches.contains(stationEdge)) matches.add(stationEdge);
        }
        return matches;
    }

    public List<StationEdge> filterByStreet(Street street) {
        List<StationEdge> matches = new ArrayList<>();
        if (street == null) return matches;

        for (StationEdge stationEdge : catalog) {
            Street headStreet = stationEdge.getHead().getBlock().getStreet();
            Street tailStreet = stationEdge.getTail().getBlock().getStreet();

            if (headStreet == null || tailStreet == null) continue;
            if (headStreet.equals(street) || tailStreet.equals(street) && !matches.contains(stationEdge)) matches.add(stationEdge);
        }
        return matches;
    }

    public List<StationEdge> filterByStation(Station station) {
        List<StationEdge> matches = new ArrayList<>();
        if (station == null) return matches;

        for (StationEdge stationEdge : catalog) {
            if (stationEdge.getTail().equals(station) || stationEdge.getHead().equals(station) && !matches.contains(stationEdge))
                matches.add(stationEdge);
        }
        return matches;
    }

    public List<StationEdge> filterByCurb(Curb curb) {
        List<StationEdge> matches = new ArrayList<>();

        if (curb == null) return matches;

        for (StationEdge stationEdge : catalog) {
            Curb headCurb = stationEdge.getHead().getBlock().getCurb();
            Curb tailCurb = stationEdge.getTail().getBlock().getCurb();

            if (headCurb == null || tailCurb == null) continue;
            if (headCurb.equals(curb) || tailCurb.equals(curb) && !matches.contains(stationEdge)) matches.add(stationEdge);
        }
        return matches;
    }
}