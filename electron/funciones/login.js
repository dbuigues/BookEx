// ============================================
        // CONFIGURACIÓN DE LA API (INVENTADA)
        // ============================================
        const API_BASE_URL = 'https://api.tudominio.com/v1';
        const API_ENDPOINTS = {
            login: `${API_BASE_URL}/auth/login`,
            verify: `${API_BASE_URL}/auth/verify`,
            user: `${API_BASE_URL}/user/profile`
        };

        // ============================================
        // VARIABLES GLOBALES
        // ============================================
        let currentUser = null;

        // ============================================
        // FUNCIONES DE ALMACENAMIENTO
        // ============================================
        
        /**
         * Guarda el token de autenticación en memoria
         * NOTA: En producción, usar localStorage: localStorage.setItem('authToken', token)
         */
        function saveToken(token) {
            window.authToken = token;
        }

        /**
         * Obtiene el token de autenticación
         * NOTA: En producción, usar: localStorage.getItem('authToken')
         */
        function getToken() {
            return window.authToken || null;
        }

        /**
         * Elimina el token de autenticación
         * NOTA: En producción, usar: localStorage.removeItem('authToken')
         */
        function clearToken() {
            window.authToken = null;
        }

        // ============================================
        // FUNCIONES DE UI
        // ============================================
        
        /**
         * Muestra el formulario de login
         */
        function showLoginForm() {
            document.getElementById('loading').classList.add('hidden');
            document.getElementById('loginForm').classList.remove('hidden');
            document.getElementById('userInfo').classList.add('hidden');
        }

        /**
         * Muestra la información del usuario
         */
        function showUserInfo(userData) {
            document.getElementById('loading').classList.add('hidden');
            document.getElementById('loginForm').classList.add('hidden');
            document.getElementById('userInfo').classList.remove('hidden');

            // Rellenar los datos del usuario en el DOM
            document.getElementById('userName').textContent = userData.name;
            document.getElementById('userEmail').textContent = userData.email;
            document.getElementById('userId').textContent = userData.id;
            document.getElementById('lastLogin').textContent = new Date(userData.lastLogin).toLocaleString('es-ES');
            document.getElementById('userBooks').textContent = userData.booksCount || '0';
        }

        /**
         * Muestra un mensaje de error
         */
        function showError(message) {
            const errorMsg = document.getElementById('errorMsg');
            errorMsg.textContent = message;
            errorMsg.style.display = 'block';
            
            // Ocultar el mensaje después de 5 segundos
            setTimeout(() => {
                errorMsg.style.display = 'none';
            }, 5000);
        }

        /**
         * Muestra un mensaje de éxito
         */
        function showSuccess(message) {
            const successMsg = document.getElementById('successMsg');
            successMsg.textContent = message;
            successMsg.style.display = 'block';
            
            setTimeout(() => {
                successMsg.style.display = 'none';
            }, 3000);
        }

        // ============================================
        // FUNCIONES DE API
        // ============================================
        
        /**
         * Realiza el login del usuario
         * @param {string} email - Email del usuario
         * @param {string} password - Contraseña del usuario
         * @returns {Promise<Object>} Datos del usuario y token
         */
        async function login(email, password) {
            try {
                // Realizar petición POST al endpoint de login
                const response = await fetch(API_ENDPOINTS.login, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ email, password })
                });

                // Verificar si la respuesta fue exitosa
                if (!response.ok) {
                    const error = await response.json();
                    throw new Error(error.message || 'Error al iniciar sesión');
                }

                // Obtener los datos de la respuesta
                const data = await response.json();
                
                // Guardar el token de autenticación
                saveToken(data.token);
                
                // Guardar información del usuario
                currentUser = data.user;
                
                return data;
            } catch (error) {
                console.error('Error en login:', error);
                throw error;
            }
        }

        /**
         * Verifica si el usuario tiene una sesión válida
         * @returns {Promise<Object|null>} Datos de sesión o null si no hay sesión válida
         */
        async function verifySession() {
            const token = getToken();
            
            // Si no hay token, no hay sesión
            if (!token) {
                return null;
            }

            try {
                // Verificar el token con el servidor
                const response = await fetch(API_ENDPOINTS.verify, {
                    method: 'GET',
                    headers: {
                        'Authorization': `Bearer ${token}`,
                        'Content-Type': 'application/json'
                    }
                });

                if (!response.ok) {
                    // Token inválido o expirado
                    clearToken();
                    return null;
                }

                const data = await response.json();
                currentUser = data.user;
                return data;
            } catch (error) {
                console.error('Error al verificar sesión:', error);
                clearToken();
                return null;
            }
        }

        /**
         * Cierra la sesión del usuario
         */
        function logout() {
            // Limpiar el token y datos del usuario
            clearToken();
            currentUser = null;
            
            // Mostrar el formulario de login
            showLoginForm();
            showSuccess('Sesión cerrada correctamente');
        }

        // ============================================
        // MANEJADORES DE EVENTOS
        // ============================================
        
        /**
         * Maneja el envío del formulario de login
         */
        document.getElementById('form').addEventListener('submit', async (e) => {
            e.preventDefault();
            
            // Obtener valores del formulario
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;
            
            // Validación básica
            if (!email || !password) {
                showError('Por favor completa todos los campos');
                return;
            }
            
            // Deshabilitar el botón durante el proceso
            const submitBtn = e.target.querySelector('button[type="submit"]');
            const originalText = submitBtn.textContent;
            submitBtn.disabled = true;
            submitBtn.textContent = 'Iniciando sesión...';
            submitBtn.style.opacity = '0.7';
            
            try {
                // Intentar hacer login
                const result = await login(email, password);
                
                // Mostrar mensaje de éxito
                showSuccess('¡Inicio de sesión exitoso!');
                
                // Mostrar información del usuario después de un breve delay
                setTimeout(() => {
                    showUserInfo(result.user);
                }, 1000);
                
            } catch (error) {
                // Mostrar error
                showError(error.message || 'Error al iniciar sesión. Verifica tus credenciales.');
            } finally {
                // Rehabilitar el botón
                submitBtn.disabled = false;
                submitBtn.textContent = originalText;
                submitBtn.style.opacity = '1';
            }
        });

        /**
         * Mejora la UX de los inputs al hacer focus
         */
        document.querySelectorAll('input').forEach(input => {
            input.addEventListener('focus', function() {
                this.style.borderColor = 'var(--accent-color)';
            });
            
            input.addEventListener('blur', function() {
                this.style.borderColor = '#ddd';
            });
        });

        /**
         * Mejora la UX de los botones con efecto hover
         */
        document.querySelectorAll('button').forEach(button => {
            button.addEventListener('mouseenter', function() {
                this.style.transform = 'translateY(-2px)';
            });
            
            button.addEventListener('mouseleave', function() {
                this.style.transform = 'translateY(0)';
            });
        });

        // ============================================
        // INICIALIZACIÓN
        // ============================================
        
        /**
         * Función que se ejecuta al cargar la página
         * Verifica si hay una sesión activa
         */
        async function init() {
            try {
                // Intentar verificar sesión existente
                const session = await verifySession();
                
                if (session && session.user) {
                    // Usuario ya logueado
                    showUserInfo(session.user);
                } else {
                    // No hay sesión, mostrar formulario de login
                    showLoginForm();
                }
            } catch (error) {
                console.error('Error al inicializar:', error);
                // En caso de error, mostrar formulario de login
                showLoginForm();
            }
        }

        // Ejecutar inicialización cuando el DOM esté listo
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', init);
        } else {
            init();
        }

        // ============================================
        // NOTAS PARA INTEGRACIÓN CON TU API REAL
        // ============================================
        /*
        PARA ADAPTAR A TU API:
        
        1. Cambia API_BASE_URL con tu dominio real
        
        2. Ajusta los endpoints según tu API:
           - API_ENDPOINTS.login: endpoint para login
           - API_ENDPOINTS.verify: endpoint para verificar token
           - API_ENDPOINTS.user: endpoint para obtener datos del usuario
        
        3. Modifica el formato de datos según tu API:
           Ejemplo actual esperado del servidor:
           {
             "token": "jwt_token_aqui",
             "user": {
               "id": "123",
               "name": "Juan Pérez",
               "email": "juan@example.com",
               "lastLogin": "2026-01-23T10:30:00Z",
               "booksCount": 5
             }
           }
        
        4. En producción, descomenta y usa localStorage:
           - saveToken: localStorage.setItem('authToken', token)
           - getToken: localStorage.getItem('authToken')
           - clearToken: localStorage.removeItem('authToken')
        
        5. Añade manejo de errores específico según los códigos HTTP de tu API
        
        6. Considera implementar:
           - Refresh tokens para sesiones largas
           - Manejo de expiración de token
           - Rate limiting en el cliente
           - CSRF protection si es necesario
        */