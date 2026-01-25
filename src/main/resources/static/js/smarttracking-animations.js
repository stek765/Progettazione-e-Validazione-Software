/**
 * SmartTracking Animations with Anime.js
 * Micro-interactions and smooth transitions for enhanced UX
 */

// Wait for DOM and anime.js to be ready
document.addEventListener('DOMContentLoaded', function () {

    // Check if anime.js is loaded
    if (typeof anime === 'undefined') {
        console.warn('Anime.js not loaded. Animations will be skipped.');
        return;
    }

    // ========================================
    // PAGE LOAD ANIMATIONS
    // ========================================

    /**
     * Fade in and slide up animation for cards
     */
    function animateCards() {
        const cards = document.querySelectorAll('.card, .stat-card, .action-card, .info-item');

        anime({
            targets: cards,
            translateY: [30, 0],
            opacity: [0, 1],
            duration: 800,
            delay: anime.stagger(100, { start: 200 }),
            easing: 'easeOutQuad'
        });
    }

    /**
     * Animate hero/header sections
     */
    function animateHero() {
        const hero = document.querySelector('.hero, .profile-header, .login-header, .register-header');
        if (!hero) return;

        anime({
            targets: hero,
            translateY: [-20, 0],
            opacity: [0, 1],
            duration: 1000,
            easing: 'easeOutExpo'
        });
    }

    /**
     * Animate navbar with slide down effect
     */
    function animateNavbar() {
        const navbar = document.querySelector('.navbar');
        if (!navbar) return;

        anime({
            targets: navbar,
            translateY: [-50, 0],
            opacity: [0, 1],
            duration: 600,
            easing: 'easeOutQuad'
        });
    }

    /**
     * Staggered animation for table rows
     */
    function animateTableRows() {
        const rows = document.querySelectorAll('tbody tr');
        if (rows.length === 0) return;

        anime({
            targets: rows,
            translateX: [-20, 0],
            opacity: [0, 1],
            duration: 600,
            delay: anime.stagger(50),
            easing: 'easeOutQuad'
        });
    }

    /**
     * Animate stats numbers with counting effect
     */
    function animateStatNumbers() {
        const statValues = document.querySelectorAll('.stat-value');

        statValues.forEach(stat => {
            const finalValue = parseInt(stat.textContent) || 0;
            const obj = { value: 0 };

            anime({
                targets: obj,
                value: finalValue,
                round: 1,
                duration: 2000,
                easing: 'easeOutExpo',
                update: function () {
                    stat.textContent = obj.value;
                }
            });
        });
    }

    // ========================================
    // BUTTON INTERACTIONS
    // ========================================

    /**
     * Add press animation to buttons
     */
    function setupButtonAnimations() {
        const buttons = document.querySelectorAll('.btn, button[type="submit"]');

        buttons.forEach(button => {
            button.addEventListener('mousedown', function () {
                anime({
                    targets: this,
                    scale: 0.95,
                    duration: 100,
                    easing: 'easeInOutQuad'
                });
            });

            button.addEventListener('mouseup', function () {
                anime({
                    targets: this,
                    scale: 1,
                    duration: 200,
                    easing: 'spring(1, 80, 10, 0)'
                });
            });

            button.addEventListener('mouseleave', function () {
                anime({
                    targets: this,
                    scale: 1,
                    duration: 200,
                    easing: 'easeOutQuad'
                });
            });
        });
    }

    /**
     * Ripple effect for buttons
     */
    function createRippleEffect(event) {
        const button = event.currentTarget;
        const ripple = document.createElement('span');
        const rect = button.getBoundingClientRect();
        const size = Math.max(rect.width, rect.height);
        const x = event.clientX - rect.left - size / 2;
        const y = event.clientY - rect.top - size / 2;

        ripple.style.width = ripple.style.height = size + 'px';
        ripple.style.left = x + 'px';
        ripple.style.top = y + 'px';
        ripple.classList.add('ripple');

        button.appendChild(ripple);

        anime({
            targets: ripple,
            scale: [0, 2.5],
            opacity: [0.5, 0],
            duration: 600,
            easing: 'easeOutExpo',
            complete: () => ripple.remove()
        });
    }

    // Add ripple CSS if not present
    if (!document.querySelector('#ripple-style')) {
        const style = document.createElement('style');
        style.id = 'ripple-style';
        style.textContent = `
            .btn, button[type="submit"] {
                position: relative;
                overflow: hidden;
            }
            .ripple {
                position: absolute;
                border-radius: 50%;
                background: rgba(255, 255, 255, 0.6);
                pointer-events: none;
            }
        `;
        document.head.appendChild(style);
    }

    const clickableButtons = document.querySelectorAll('.btn:not(.btn-logout), button[type="submit"]');
    clickableButtons.forEach(btn => {
        btn.addEventListener('click', createRippleEffect);
    });

    // ========================================
    // FORM INPUT ANIMATIONS
    // ========================================

    /**
     * Float label animation for form inputs
     */
    function setupInputAnimations() {
        const inputs = document.querySelectorAll('.form-input, .form-select, .form-textarea');

        inputs.forEach(input => {
            // Focus animation
            input.addEventListener('focus', function () {
                anime({
                    targets: this,
                    scale: [1, 1.02],
                    duration: 200,
                    easing: 'easeOutQuad'
                });
            });

            // Blur animation
            input.addEventListener('blur', function () {
                anime({
                    targets: this,
                    scale: [1.02, 1],
                    duration: 200,
                    easing: 'easeOutQuad'
                });
            });
        });
    }

    /**
     * Shake animation for error inputs
     */
    function shakeErrorInputs() {
        const errorInputs = document.querySelectorAll('.form-group.error .form-input, .form-error');

        if (errorInputs.length > 0) {
            anime({
                targets: errorInputs,
                translateX: [
                    { value: -10, duration: 50 },
                    { value: 10, duration: 50 },
                    { value: -10, duration: 50 },
                    { value: 10, duration: 50 },
                    { value: 0, duration: 50 }
                ],
                easing: 'easeInOutSine'
            });
        }
    }

    // ========================================
    // ALERT & TOAST ANIMATIONS
    // ========================================

    /**
     * Slide in animation for alerts
     */
    function animateAlerts() {
        const alerts = document.querySelectorAll('.alert');

        alerts.forEach(alert => {
            anime({
                targets: alert,
                translateX: [50, 0],
                opacity: [0, 1],
                duration: 500,
                easing: 'easeOutExpo'
            });
        });
    }

    /**
     * Create toast notification
     */
    window.showToast = function (message, type) {
        if (type === undefined) type = 'success';
        const toast = document.createElement('div');
        toast.className = `toast toast-${type}`;
        toast.innerHTML = `
            <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'}"></i>
            <span>${message}</span>
        `;
        document.body.appendChild(toast);

        anime({
            targets: toast,
            translateX: [400, 0],
            opacity: [0, 1],
            duration: 500,
            easing: 'easeOutExpo'
        });

        setTimeout(() => {
            anime({
                targets: toast,
                translateX: [0, 400],
                opacity: [1, 0],
                duration: 400,
                easing: 'easeInExpo',
                complete: () => toast.remove()
            });
        }, 3000);
    };

    // ========================================
    // BADGE & ICON ANIMATIONS
    // ========================================

    /**
     * Pulse animation for badges
     */
    function animateBadges() {
        const badges = document.querySelectorAll('.badge, .stat-badge');

        anime({
            targets: badges,
            scale: [0.8, 1],
            opacity: [0, 1],
            duration: 600,
            delay: anime.stagger(50, { start: 400 }),
            easing: 'easeOutElastic(1, .6)'
        });
    }

    /**
     * Rotate animation for icons on hover
     */
    function setupIconAnimations() {
        const actionCards = document.querySelectorAll('.action-card');

        actionCards.forEach(card => {
            const icon = card.querySelector('.action-icon i, i');
            if (!icon) return;

            card.addEventListener('mouseenter', function () {
                anime({
                    targets: icon,
                    rotate: [0, 360],
                    duration: 600,
                    easing: 'easeInOutSine'
                });
            });
        });
    }

    // ========================================
    // NAVIGATION TRANSITIONS
    // ========================================

    /**
     * Smooth page transitions
     */
    function setupPageTransitions() {
        const links = document.querySelectorAll('a:not([target="_blank"])');

        links.forEach(link => {
            link.addEventListener('click', function (e) {
                // Skip for external links and logout
                if (this.href.includes('http') && !this.href.includes(window.location.host)) return;
                if (this.href.includes('/logout')) return;
                if (this.href.includes('#')) return;

                e.preventDefault();
                const href = this.href;

                anime({
                    targets: document.body,
                    opacity: [1, 0],
                    duration: 300,
                    easing: 'easeInQuad',
                    complete: () => {
                        window.location.href = href;
                    }
                });
            });
        });
    }

    // ========================================
    // SCROLL ANIMATIONS
    // ========================================

    /**
     * Animate elements on scroll
     */
    function setupScrollAnimations() {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    anime({
                        targets: entry.target,
                        translateY: [30, 0],
                        opacity: [0, 1],
                        duration: 800,
                        easing: 'easeOutQuad'
                    });
                    observer.unobserve(entry.target);
                }
            });
        }, { threshold: 0.1 });

        const animatableElements = document.querySelectorAll('.card, .stat-card');
        animatableElements.forEach(el => observer.observe(el));
    }

    // ========================================
    // INITIALIZE ALL ANIMATIONS
    // ========================================

    // Run animations in sequence
    setTimeout(() => {
        animateNavbar();
        animateHero();
        animateCards();
        animateTableRows();
        animateStatNumbers();
        animateBadges();
        animateAlerts();
        shakeErrorInputs();

        setupButtonAnimations();
        setupInputAnimations();
        setupIconAnimations();
        setupScrollAnimations();
        // Removed setupPageTransitions() to avoid interfering with form submissions
    }, 100);

    console.log('✨ SmartTracking animations loaded successfully');
});

/**
 * Loading spinner animation
 */
window.showLoadingSpinner = function (element) {
    const spinner = document.createElement('div');
    spinner.className = 'spinner';
    element.appendChild(spinner);

    anime({
        targets: spinner,
        rotate: '1turn',
        duration: 1000,
        loop: true,
        easing: 'linear'
    });

    return spinner;
};

/**
 * Success checkmark animation
 */
window.showSuccessCheckmark = function (element) {
    const checkmark = document.createElement('i');
    checkmark.className = 'fas fa-check-circle';
    checkmark.style.fontSize = '3rem';
    checkmark.style.color = 'var(--success)';
    element.appendChild(checkmark);

    anime({
        targets: checkmark,
        scale: [0, 1],
        opacity: [0, 1],
        duration: 600,
        easing: 'easeOutElastic(1, .8)'
    });

    return checkmark;
};
