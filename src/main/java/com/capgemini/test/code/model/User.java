package com.capgemini.test.code.model;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false, length = 6)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String dni;

    public User() {}

    public User(Room room, String name, String email, String phone, Role role, String dni) {
        this.room = room;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.dni = dni;
    }

    public Long getId() { return id; }
    public Room getRoom() { return room; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public Role getRole() { return role; }
    public String getDni() { return dni; }
}
