export function Footer() {
    return (
        <footer className="border-t border-white/[0.06] px-8 md:px-12 py-6 flex flex-col md:flex-row items-center justify-between gap-4 text-[13px] text-gray-600">
            <span>© 2026 Journi.dev. All rights reserved.</span>
            <div className="flex gap-6">
                <a
                    href="#"
                    className="hover:text-gray-400 transition-colors"
                >
                    Terms
                </a>
                <a
                    href="#"
                    className="hover:text-gray-400 transition-colors"
                >
                    Privacy
                </a>
                <a
                    href="#"
                    className="hover:text-gray-400 transition-colors"
                >
                    System Status
                </a>
            </div>
        </footer>
    );
}