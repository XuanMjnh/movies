document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.app-alert').forEach(alert => {
        setTimeout(() => {
            alert.style.transition = 'opacity 0.4s ease';
            alert.style.opacity = '0';
            setTimeout(() => alert.remove(), 400);
        }, 3500);
    });

    const root = document.documentElement;
    const storageKey = 'rostream-theme';
    const defaultTheme = 'dark';
    const metaTheme = document.querySelector('meta[name="theme-color"]');

    const syncThemeColor = (theme) => {
        if (!metaTheme) return;
        metaTheme.setAttribute('content', theme === 'light' ? '#fffaf1' : '#14161b');
    };

    const applyTheme = (theme, persist = true) => {
        const resolved = theme === 'light' ? 'light' : 'dark';
        root.setAttribute('data-theme', resolved);
        syncThemeColor(resolved);
        if (persist) {
            localStorage.setItem(storageKey, resolved);
        }
    };

    const savedTheme = localStorage.getItem(storageKey);
    applyTheme(savedTheme || defaultTheme, false);

    if (savedTheme) {
        localStorage.setItem(storageKey, savedTheme);
    }

    document.querySelectorAll('[data-theme-toggle]').forEach(button => {
        button.addEventListener('click', () => {
            const nextTheme = root.getAttribute('data-theme') === 'light' ? 'dark' : 'light';
            applyTheme(nextTheme);
        });
    });

    const navbar = document.querySelector('.app-navbar');
    const isHomePage = document.body.classList.contains('home-page');

    if (navbar && isHomePage) {
        const toggleNavbarScrolled = () => {
            if (window.scrollY > 24) {
                navbar.classList.add('navbar-scrolled');
            } else {
                navbar.classList.remove('navbar-scrolled');
            }
        };

        toggleNavbarScrolled();
        window.addEventListener('scroll', toggleNavbarScrolled, { passive: true });
    }

    const heroCarousel = document.getElementById('heroCarousel');
    if (heroCarousel && window.bootstrap?.Carousel) {
        const animateHeroContent = (item) => {
            if (!item) {
                return;
            }

            item.classList.remove('hero-content-animated');
            void item.offsetWidth;
            item.classList.add('hero-content-animated');
        };

        heroCarousel.querySelectorAll('.carousel-item').forEach(item => {
            item.classList.remove('hero-content-animated');
        });

        animateHeroContent(heroCarousel.querySelector('.carousel-item.active'));

        const carousel = bootstrap.Carousel.getOrCreateInstance(heroCarousel, {
            interval: 7000,
            ride: 'carousel',
            pause: false,
            touch: true,
            wrap: true
        });

        heroCarousel.addEventListener('slide.bs.carousel', event => {
            heroCarousel.querySelectorAll('.carousel-item.hero-content-animated').forEach(item => {
                item.classList.remove('hero-content-animated');
            });
            animateHeroContent(event.relatedTarget);
        });

        carousel.cycle();
    }
});
