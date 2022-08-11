package nl.iobyte.framework.structures.bimap;

import java.util.Objects;

public record BiKey<K1, K2>(K1 key1, K2 key2) {

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof BiKey<?, ?> biKey)) return false;
        if(!key1.equals(biKey.key1)) return false;
        return key2.equals(biKey.key2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key1, key2);
    }

}
