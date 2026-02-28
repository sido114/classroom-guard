import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: 'standalone', // Crucial for Docker
};

export default nextConfig;
