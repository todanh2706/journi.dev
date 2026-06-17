import React, { useEffect, useState } from 'react';

interface SlideUpTransitionProps {
  children: React.ReactNode;
  bgClass?: string;
}

export const SlideUpTransition: React.FC<SlideUpTransitionProps> = ({ children, bgClass = "before:bg-[#0d0e1a]" }) => {
  const [isVisible, setIsVisible] = useState(false);

  useEffect(() => {
    // A double requestAnimationFrame ensures the browser completes painting the 
    // initial state (translate-y-4) to the screen BEFORE we change it.
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

  // We use a ::before pseudo-element that stretches upwards (bottom-full, h-16) 
  // with the same background color. This ensures that when the component is 
  // translated down, the gap at the top is covered by the component itself.
  return (
    <div
      className={`w-full transition-all duration-500 ease-out transform relative ${
        isVisible ? 'translate-y-0' : 'translate-y-4'
      } before:content-[''] before:absolute before:inset-x-0 before:bottom-full before:h-16 ${bgClass}`}
    >
      {children}
    </div>
  );
};
