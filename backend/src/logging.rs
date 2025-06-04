use chrono::Local;
use std::fs;
use std::io::{Error, ErrorKind};
use std::path::PathBuf;
use tracing_appender::non_blocking::WorkerGuard;
use tracing_subscriber::{
    fmt::format::FmtSpan, layer::SubscriberExt, util::SubscriberInitExt, EnvFilter, Layer,
};

/// Initializes the logging system for the application.
///
/// Sets up structured JSON logging to daily-rotated files, categorized by log type.
/// Log types are determined by trace targets: `system_events`, `user_events`, `admin_events`.
///
/// # Returns
/// A `Result` containing a tuple of `WorkerGuard`s or an `std::io::Error`.
/// These guards must be kept in scope for the duration of the application to ensure logs are flushed.
pub fn init_logging() -> Result<(WorkerGuard, WorkerGuard, WorkerGuard), Error> {
    let log_dir = PathBuf::from("logs");
    let today_str = Local::now().format("%Y-%m-%d").to_string();
    let daily_log_dir = log_dir.join(today_str);

    // Create base log directory and daily log directory if they don't exist
    fs::create_dir_all(&daily_log_dir)?;

    // --- System Logs ---
    let _system_log_file = daily_log_dir.join("system_logs.jsonl");
    let (system_writer, system_guard) = tracing_appender::non_blocking(
        tracing_appender::rolling::never(daily_log_dir.clone(), "system_logs.jsonl"),
    );
    let system_layer = tracing_subscriber::fmt::layer()
        .json()
        .with_writer(system_writer)
        .with_span_events(FmtSpan::CLOSE) // Include span open/close events
        .with_filter(EnvFilter::new(
            "info,chatapp_by_aarchangel_backend[system_events]=trace,system_events=trace",
        ));

    // --- User Logs --- (Example, adjust target name as needed)
    let _user_log_file = daily_log_dir.join("user_logs.jsonl");
    let (user_writer, user_guard) = tracing_appender::non_blocking(
        tracing_appender::rolling::never(daily_log_dir.clone(), "user_logs.jsonl"),
    );
    let user_layer = tracing_subscriber::fmt::layer()
        .json()
        .with_writer(user_writer)
        .with_span_events(FmtSpan::CLOSE)
        .with_filter(EnvFilter::new(
            "info,chatapp_by_aarchangel_backend[user_events]=trace,user_events=trace",
        ));

    // --- Admin Logs --- (Example, adjust target name as needed)
    let _admin_log_file = daily_log_dir.join("admin_logs.jsonl");
    let (admin_writer, admin_guard) = tracing_appender::non_blocking(
        tracing_appender::rolling::never(daily_log_dir.clone(), "admin_logs.jsonl"),
    );
    let admin_layer = tracing_subscriber::fmt::layer()
        .json()
        .with_writer(admin_writer)
        .with_span_events(FmtSpan::CLOSE)
        .with_filter(EnvFilter::new(
            "info,chatapp_by_aarchangel_backend[admin_events]=trace,admin_events=trace",
        ));

    // General console layer for fallback/other logs, or for development
    let _console_env_filter =
        EnvFilter::try_from_default_env().unwrap_or_else(|_| EnvFilter::new("info")); // Default to info if RUST_LOG is not set
    let console_layer = tracing_subscriber::fmt::layer()
        .with_writer(std::io::stdout)
        .with_filter(console_layer_filter_excluding_custom_targets());

    // Combine layers and initialize the global subscriber
    tracing_subscriber::registry()
        .with(system_layer)
        .with(user_layer)
        .with(admin_layer)
        .with(console_layer) // Add console layer last as a general fallback
        .try_init()
        .map_err(|e| {
            Error::new(
                ErrorKind::Other,
                format!("Failed to initialize logger: {}", e),
            )
        })?;

    Ok((system_guard, user_guard, admin_guard))
}

/// Creates an EnvFilter for the console layer that excludes custom targets
/// to prevent duplicate logging to console if those targets are also captured by file layers.
fn console_layer_filter_excluding_custom_targets() -> EnvFilter {
    EnvFilter::try_from_default_env()
        .unwrap_or_else(|_| EnvFilter::new("info"))
        .add_directive("system_events=off".parse().unwrap()) // Turn off system_events for console
        .add_directive("user_events=off".parse().unwrap()) // Turn off user_events for console
        .add_directive("admin_events=off".parse().unwrap()) // Turn off admin_events for console
}
