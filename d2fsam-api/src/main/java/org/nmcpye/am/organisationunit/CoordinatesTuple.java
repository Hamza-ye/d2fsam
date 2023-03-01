package org.nmcpye.am.organisationunit;

import java.util.ArrayList;
import java.util.List;

public class CoordinatesTuple {

    private List<String> coordinatesTuple = new ArrayList<>();

    public void addCoordinates(String coordinates) {
        this.coordinatesTuple.add(coordinates);
    }

    public long getNumberOfCoordinates() {
        return this.coordinatesTuple.size();
    }

    public List<String> getCoordinatesTuple() {
        return coordinatesTuple;
    }

    public boolean hasCoordinates() {
        return this.coordinatesTuple != null && this.coordinatesTuple.size() > 0;
    }

    public static boolean hasCoordinates(List<CoordinatesTuple> list) {
        if (list != null && list.size() > 0) {
            for (CoordinatesTuple tuple : list) {
                if (tuple.hasCoordinates()) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        for (String c : coordinatesTuple) {
            result = prime * result + c.hashCode();
        }

        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        if (getClass() != o.getClass()) {
            return false;
        }

        final CoordinatesTuple other = (CoordinatesTuple) o;

        if (coordinatesTuple.size() != other.getCoordinatesTuple().size()) {
            return false;
        }

        int size = coordinatesTuple.size();

        for (int i = 0; i < size; i++) {
            if (!coordinatesTuple.get(i).equals(other.getCoordinatesTuple().get(i))) {
                return false;
            }
        }

        return true;
    }
}
