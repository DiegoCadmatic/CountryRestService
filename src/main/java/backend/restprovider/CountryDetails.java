package backend.restprovider;

import com.fasterxml.jackson.annotation.*;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "name", "country_code", "capital", "population", "flag_file_url"})
public final class CountryDetails {
    @JsonProperty("name")
    private String name;

    @JsonProperty("country_code")
    private String countryCode;

    @JsonProperty("capital")
    private String capital;

    @JsonProperty("population")
    private long population;

    @JsonProperty("flag_file_url")
    private String flagFileUrl;

    @JsonCreator
    public CountryDetails(@JsonProperty("name") String name, @JsonProperty("alpha2Code") String countryCode,
                          @JsonProperty("capital") String capital, @JsonProperty("population") long population,
                          @JsonProperty("flag") String flag) {
        this.name = name;
        this.countryCode = countryCode;
        this.capital = capital;
        this.population = population;
        this.flagFileUrl = flag;
    }

    public String getName() {
        return name;
    }

    @JsonIgnore
    public String getCountryCode() {
        return countryCode;
    }

    public String getCapital() {
        return capital;
    }

    public long getPopulation() {
        return population;
    }

    @JsonIgnore
    public String getFlagFileUrl() {
        return flagFileUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CountryDetails that = (CountryDetails) o;
        return population == that.population && Objects.equals(name, that.name) && Objects.equals(countryCode, that.countryCode) && Objects.equals(capital, that.capital) && Objects.equals(flagFileUrl, that.flagFileUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, countryCode, capital, population, flagFileUrl);
    }
}