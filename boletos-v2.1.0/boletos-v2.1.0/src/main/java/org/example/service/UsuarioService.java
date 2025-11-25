package org.example.service;

import org.example.entity.Usuario;
import org.example.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;

public class UsuarioService {
    private UsuarioRepository usuarioRepository;

    public UsuarioService() {
        this.usuarioRepository = new UsuarioRepository();
    }

    public Usuario registrarUsuario(Usuario usuario) {
        // Verificar si el email ya existe
        if (usuarioRepository.existeEmail(usuario.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Asegurar que el rol sea USER para nuevos registros
        if (usuario.getRol() == null) {
            usuario.setRol("USER");
        }

        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> login(String email, String password) {
        return usuarioRepository.findByEmailAndPassword(email, password);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public boolean eliminarUsuario(Long id) {
        return usuarioRepository.delete(id);
    }

    public boolean esAdmin(Usuario usuario) {
        return usuario != null && "ADMIN".equals(usuario.getRol());
    }
}