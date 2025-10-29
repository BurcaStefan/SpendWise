package com.example.spendwise.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_token")
public class RefreshToken {

    @Id
    @Column(name = "token_id", nullable = false)
    private UUID tokenId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(name = "token", nullable = false)
    private String token;

    @Column(name = "createDate", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "expirationDate", nullable = false)
    private LocalDateTime expirationDate;

    public RefreshToken() {}

    public RefreshToken(UUID tokenId, User user, String token, LocalDateTime createDate, LocalDateTime expirationDate) {
        this.tokenId = tokenId;
        this.user = user;
        this.token = token;
        this.createDate = createDate;
        this.expirationDate = expirationDate;
    }

    public UUID getTokenId() { return tokenId; }
    public void setTokenId(UUID tokenId) { this.tokenId = tokenId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public LocalDateTime getCreateDate() { return createDate; }
    public void setCreateDate(LocalDateTime createDate) { this.createDate = createDate; }

    public LocalDateTime getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDateTime expirationDate) { this.expirationDate = expirationDate; }
}
