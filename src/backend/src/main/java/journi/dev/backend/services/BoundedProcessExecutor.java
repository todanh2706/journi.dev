package journi.dev.backend.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public class BoundedProcessExecutor {
    public ProcessResult execute(List<String> command, Path workingDirectory, Duration timeout, int maxOutputBytes)
            throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder(command)
                .directory(workingDirectory.toFile())
                .redirectErrorStream(true);
        Process process = processBuilder.start();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> output = executor.submit(() -> readBounded(process.getInputStream(), maxOutputBytes));
            boolean completed = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
            if (!completed) {
                process.destroyForcibly();
                process.waitFor(5, TimeUnit.SECONDS);
            }
            try {
                return new ProcessResult(completed ? process.exitValue() : -1, !completed, output.get());
            } catch (ExecutionException exception) {
                Throwable cause = exception.getCause();
                if (cause instanceof IOException ioException) {
                    throw ioException;
                }
                throw new IOException("Failed to capture process output", cause);
            }
        }
    }

    private String readBounded(InputStream input, int maxOutputBytes) throws IOException {
        ByteArrayOutputStream captured = new ByteArrayOutputStream(Math.min(maxOutputBytes, 8192));
        byte[] buffer = new byte[8192];
        int read;
        int remaining = maxOutputBytes;
        while ((read = input.read(buffer)) != -1) {
            if (remaining > 0) {
                int accepted = Math.min(read, remaining);
                captured.write(buffer, 0, accepted);
                remaining -= accepted;
            }
        }
        String output = captured.toString(StandardCharsets.UTF_8);
        return remaining == 0 ? output + "\n[output truncated]" : output;
    }

    public record ProcessResult(int exitCode, boolean timedOut, String output) {
        public boolean succeeded() {
            return !timedOut && exitCode == 0;
        }
    }
}
