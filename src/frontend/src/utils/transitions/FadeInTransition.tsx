import React, { useEffect, useState } from 'react';

interface FadeInTransitionProps {
  children: React.ReactNode;
}

export const FadeInTransition: React.FC<FadeInTransitionProps> = ({ children }) => {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    // A double requestAnimationFrame ensures the browser completes painting the 
    // initial state (opacity-0) to the screen BEFORE we change it to opacity-100.
    // This strictly enforces that the CSS transition runs.
    let raf2: number;
    const raf1 = requestAnimationFrame(() => {
      raf2 = requestAnimationFrame(() => {
        setIsVisible(true);
      });
    });

    return () => {
      cancelAnimationFrame(raf1);
      cancelAnimationFrame(raf2);
    };
  }, []);

  return (
    <div
      className={`transition-opacity duration-500 ease-in-out ${
        isVisible ? 'opacity-100' : 'opacity-0'
      }`}
    >
      {children}
    </div>
  );
};
