import { Link } from "react-router-dom";

export default function SignUp() {
    return (
        <div className="min-h-screen bg-[#0d0e1a] text-gray-300 flex flex-col items-center justify-center font-sans">
            <h1 className="text-4xl font-bold text-white mb-4">Join Journi.dev</h1>
            <p className="text-gray-500 mb-8 text-lg">Work in progress – Check back soon!</p>
            <Link 
                to="/" 
                className="bg-purple-600 text-white px-6 py-2 rounded-lg hover:bg-purple-500 transition-colors"
            >
                Back to Home
            </Link>
        </div>
    );
}
