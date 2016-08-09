package ru.skuptsov.sphinx.console.coordinator.model;


import ru.skuptsov.sphinx.console.coordinator.model.common.BaseEntity;
import ru.skuptsov.sphinx.console.coordinator.validation.constraints.Ip;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class Server extends BaseEntity {
	private Long id;

	@NotNull
	@Ip
	private String ip;

	private String domain;

	@NotNull
	@Size(max = 25)
	private String name;
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{id:" + id + ", ip:" + ip + ", domain:" + domain + ", name:" + name+"}";
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		Server server = (Server) o;

		if (domain != null ? !domain.equals(server.domain) : server.domain != null) return false;
		if (ip != null ? !ip.equals(server.ip) : server.ip != null) return false;
		if (name != null ? !name.equals(server.name) : server.name != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (ip != null ? ip.hashCode() : 0);
		result = 31 * result + (domain != null ? domain.hashCode() : 0);
		result = 31 * result + (name != null ? name.hashCode() : 0);
		return result;
	}
}
